package com.zggis.howler.services;

import com.google.common.eventbus.EventBus;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.exceptions.InvalidDataSourceException;
import com.zggis.howler.repositories.AlertRepo;
import com.zggis.howler.repositories.DataSourceRepo;
import com.zggis.howler.runners.FileWatcher;
import com.zggis.howler.runners.FolderWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private DataSourceRepo dataSourceRepo;

    @Autowired
    private AlertRepo alertRepo;

    private final Map<Long, EventBus> eventBusses = new HashMap<>();

    private final Map<Long, ExecutorService> executors = new HashMap<>();

    private WatchService watchService;

    @PostConstruct
    public void init() {
        try {
            watchService
                    = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        for (DataSourceEntity entity : dataSourceRepo.findAll()) {
            setupDataSource(entity);
        }
    }

    @Override
    @Transactional
    public DataSourceEntity add(DataSourceEntity entity) throws InvalidDataSourceException {
        if (CollectionUtils.isEmpty(dataSourceRepo.findByPath(entity.getPath()))) {
            DataSourceEntity saveResult = dataSourceRepo.save(entity);
            setupDataSource(saveResult);
            return saveResult;
        }
        throw new InvalidDataSourceException("Could not add Data Source for " + entity.getPath() + " since one already exists for that path", 410);
    }

    private void setupDataSource(DataSourceEntity saveResult) {
        logger.info("Setting up Data Source {}", saveResult.getPath());
        EventBus eventBus = new EventBus();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        eventBusses.put(saveResult.getId(), eventBus);
        executors.put(saveResult.getId(), executor);
        File dir = new File(saveResult.getPath());
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().endsWith(".txt") || child.getName().endsWith(".log")) {
                    executor.execute(new FileWatcher(child, eventBus));
                }
            }
        }
        executor.execute(new FolderWatcher(watchService, saveResult.getPath(), executor, eventBus));
    }

    @Override
    public Optional<DataSourceEntity> findById(Long id) {
        return dataSourceRepo.findById(id);
    }

    @Override
    public Iterable<DataSourceEntity> findAll() {
        return dataSourceRepo.findAll();
    }

    @Override
    public EventBus getEventBus(Long dataSourceId) {
        return eventBusses.get(dataSourceId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting Data Source {}", id);
        dataSourceRepo.deleteById(id);
        ExecutorService executorService = executors.remove(id);
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        for (AlertEntity deletedAlert : alertRepo.deleteByDataSourceId(id)) {
            logger.info("{} alert was deleted since it was connected to the deleted Data Source {}", deletedAlert.getName(), id);
        }
        eventBusses.remove(id);
    }

}

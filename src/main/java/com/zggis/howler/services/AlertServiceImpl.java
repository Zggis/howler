package com.zggis.howler.services;

import com.google.common.eventbus.EventBus;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.listeners.DiscordEventListener;
import com.zggis.howler.repositories.AlertRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private DataSourceService dataSourceService;

    private final Map<Long, DiscordEventListener> listeners = new HashMap<>();

    @PostConstruct
    public void init() {
        for (AlertEntity entity : alertRepo.findAll()) {
            setupAlert(entity);
        }
    }

    @Override
    public AlertEntity add(AlertEntity entity) {
        AlertEntity savedAlert = alertRepo.save(entity);
        setupAlert(savedAlert);
        return savedAlert;
    }

    private void setupAlert(AlertEntity savedAlert) {
        logger.info("Setting up Alert {} to match '{}'", savedAlert.getName(), savedAlert.getMatchingString());
        EventBus eventBus = dataSourceService.getEventBus(savedAlert.getDataSourceId());
        DiscordEventListener listener = new DiscordEventListener(savedAlert);
        eventBus.register(listener);
        listeners.put(savedAlert.getId(), listener);
    }

    @Override
    public Optional<AlertEntity> findById(Long id) {
        return alertRepo.findById(id);
    }

    @Override
    public Iterable<AlertEntity> findAll() {
        return alertRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Optional<AlertEntity> findById = alertRepo.findById(id);
        if (findById.isPresent()) {
            EventBus eventBus = dataSourceService.getEventBus(findById.get().getDataSourceId());
            eventBus.unregister(listeners.get(id));
            alertRepo.deleteById(id);
        }
    }
}

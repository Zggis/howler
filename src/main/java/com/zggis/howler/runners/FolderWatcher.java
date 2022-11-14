package com.zggis.howler.runners;

import com.google.common.eventbus.EventBus;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FolderWatcher.class);

    private final String directory;

    private final WatchService watchService;

    private final EventBus eventBus;

    private final ExecutorService executor;

    public FolderWatcher(WatchService watchService, String directory, ExecutorService executor, EventBus eventBus) {
        this.watchService = watchService;
        this.directory = directory;
        this.executor = executor;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        Path path = Paths.get(directory);
        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().endsWith(".txt") || event.context().toString().endsWith(".log")) {
                        File file = new File(directory + "/" + event.context());
                        executor.execute(new FileWatcher(file, eventBus));
                        logger.info("Add {} to datasource", directory + "/" + event.context());
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

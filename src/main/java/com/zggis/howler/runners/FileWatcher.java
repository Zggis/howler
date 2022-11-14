package com.zggis.howler.runners;

import com.google.common.eventbus.EventBus;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class);

    private final File file;

    private final EventBus eventBus;

    public FileWatcher(File file, EventBus eventBus) {
        this.file = file;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        Tailer tailer = new Tailer(file, new MyListener(eventBus), 500, true, true);
        tailer.run();
        logger.info("Removed {} from datasource", file.getName());
    }

    private static class MyListener extends TailerListenerAdapter {

        private final EventBus eventBus;

        public MyListener(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        @Override
        public void handle(String line) {
            this.eventBus.post(line);
        }

    }
}

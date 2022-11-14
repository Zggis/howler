package com.zggis.howler.listeners;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TestEventListener.class);

    @Subscribe
    public void stringEvent(String event) {
        logger.info(event);
    }
}

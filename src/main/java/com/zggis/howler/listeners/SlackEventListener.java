package com.zggis.howler.listeners;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.zggis.howler.entity.AlertEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SlackEventListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(SlackEventListener.class);

    private final AlertEntity alert;

    public SlackEventListener(AlertEntity alert) {
        this.alert = alert;
    }

    @Override
    public void stringEvent(String event) {
        Payload payload = Payload.builder().text(event).build();
        try {
            Slack.getInstance().send(alert.getWebhookUrl(), payload);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void test() {
        logger.info("Testing alert {}", alert.getName());
        Payload payload = Payload.builder().text("Testing - " + alert.getName()).build();
        try {
            Slack.getInstance().send(alert.getWebhookUrl(), payload);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}

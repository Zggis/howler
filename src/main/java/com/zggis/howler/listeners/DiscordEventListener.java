package com.zggis.howler.listeners;

import com.google.common.eventbus.Subscribe;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.utils.DiscordWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class DiscordEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DiscordEventListener.class);
    public static final String THMB_URL = "https://raw.githubusercontent.com/Zggis/howler/master/favicon.webp";

    private final DiscordWebhook discordWebhook;

    private final AlertEntity alert;

    public DiscordEventListener(AlertEntity alert) {
        discordWebhook = new DiscordWebhook(alert.getWebhookUrl());
        this.alert = alert;
    }

    @Subscribe
    public void stringEvent(String event) {
        logger.debug("New Event: {}", event);
        if (event.toLowerCase().contains(alert.getMatchingString().toLowerCase())) {
            discordWebhook.setContent("Alert - " + alert.getName() + " was triggered!");
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setColor(Color.GREEN);
            embed.setDescription(event);
            embed.setThumbnail(THMB_URL);
            discordWebhook.addEmbed(embed);
            try {
                discordWebhook.execute();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void test() {
        logger.info("Testing alert {}", alert.getName());
        discordWebhook.setContent("Testing - " + alert.getName());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setColor(Color.GREEN);
        embed.setDescription("This is a test message.");
        embed.setThumbnail(THMB_URL);
        discordWebhook.addEmbed(embed);
        try {
            discordWebhook.execute();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

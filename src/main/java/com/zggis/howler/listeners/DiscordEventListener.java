package com.zggis.howler.listeners;

import com.google.common.eventbus.Subscribe;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.utils.DiscordWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DiscordEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DiscordEventListener.class);

    private DiscordWebhook discordWebhook;

    private AlertEntity alert;

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
            embed.setDescription(event);
            //embed.setThumbnail("");
            discordWebhook.addEmbed(embed);
            try {
                discordWebhook.execute();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}

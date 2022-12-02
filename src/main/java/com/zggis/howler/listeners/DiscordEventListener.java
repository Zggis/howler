package com.zggis.howler.listeners;

import com.mgnt.utils.StringUnicodeEncoderDecoder;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.utils.DiscordWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class DiscordEventListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(DiscordEventListener.class);
    public static final String THMB_URL = "https://raw.githubusercontent.com/Zggis/howler/master/favicon.webp";

    private final DiscordWebhook discordWebhook;

    private final AlertEntity alert;

    public DiscordEventListener(AlertEntity alert) {
        discordWebhook = new DiscordWebhook(alert.getWebhookUrl());
        this.alert = alert;
    }

    @Override
    public void stringEvent(String event) {
        logger.debug("New Event: {}", event);
        if (event.toLowerCase().contains(alert.getMatchingString().toLowerCase())) {
            discordWebhook.setUsername(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(alert.getUsername()));
            discordWebhook.setContent("Alert - " + StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(alert.getName()) + " was triggered!");
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
            embed.setColor(getColor());
            embed.setDescription(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(event));
            embed.setThumbnail(THMB_URL);
            discordWebhook.addEmbed(embed);
            try {
                discordWebhook.execute();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void test() {
        logger.info("Testing alert {}", alert.getName());
        discordWebhook.setUsername(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(alert.getUsername()));
        discordWebhook.setContent("Testing - " + StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(alert.getName()));
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setColor(getColor());
        embed.setDescription("This is a test message. Log text will be displayed here.");
        embed.setThumbnail(THMB_URL);
        discordWebhook.addEmbed(embed);
        try {
            discordWebhook.execute();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Color getColor() {
        Color c = Color.GREEN;
        if ("RED".equals(alert.getColor())) {
            c = Color.RED;
        } else if ("BLUE".equals(alert.getColor())) {
            c = Color.BLUE;
        } else if ("YELLOW".equals(alert.getColor())) {
            c = Color.YELLOW;
        }
        return c;
    }
}

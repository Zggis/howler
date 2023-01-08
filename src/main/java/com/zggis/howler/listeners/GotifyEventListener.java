package com.zggis.howler.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.utils.LogMatcher;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GotifyEventListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(GotifyEventListener.class);

    private final AlertEntity alert;

    private final String gotifyUrl;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public GotifyEventListener(AlertEntity alert) {
        this.alert = alert;
        this.gotifyUrl = String.format("%s/message?token=%s", alert.getServerUrl(), alert.getToken());
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void stringEvent(String event) {
        logger.debug("New Event: {}", event);
        if (LogMatcher.check(alert.getMatchingString(), event, alert.isRegularExp())) {
            try {
                sendMessage(new Message("Alert - " + alert.getName(), event, 1));
            } catch (IOException | InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void test() {
        logger.info("Testing alert {}", alert.getName());
        try {
            sendMessage(new Message("Alert - " + alert.getName(), "This is a test Alert!", 1));
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendMessage(Message message) throws IOException, InterruptedException {
        final var bodyData = objectMapper.writeValueAsString(message);
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(gotifyUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyData))
                .build();
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug(response.body());
    }

    @Getter
    @Setter
    private static class Message {
        private String message;
        private String title;
        private int priority;

        public Message(String title, String message, int priority) {
            this.message = message;
            this.priority = priority;
            this.title = title;
        }
    }

}

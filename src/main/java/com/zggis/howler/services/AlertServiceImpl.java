package com.zggis.howler.services;

import com.google.common.eventbus.EventBus;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.exceptions.InvalidAlertException;
import com.zggis.howler.listeners.DiscordEventListener;
import com.zggis.howler.listeners.EventListener;
import com.zggis.howler.listeners.GotifyEventListener;
import com.zggis.howler.listeners.SlackEventListener;
import com.zggis.howler.repositories.AlertRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    private static final Pattern WEBHOOK_PATTERN = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private DataSourceService dataSourceService;

    private final Map<Long, EventListener> listeners = new HashMap<>();

    @PostConstruct
    public void init() {
        for (AlertEntity entity : alertRepo.findAll()) {
            setupAlert(entity);
        }
    }

    @Override
    @Transactional
    public AlertEntity add(AlertEntity newEntity) throws InvalidAlertException {
        validateAlert(newEntity);
        AlertEntity entity = sanitize(newEntity);
        Collection<AlertEntity> existingAlert = alertRepo.findByNameAndDataSourceIdAndMatchingString(entity.getName(), entity.getDataSourceId(), entity.getMatchingString());
        if (!CollectionUtils.isEmpty(existingAlert)) {
            throw new InvalidAlertException("Alert was not created because an Alert with the same name, data source, and matching string exists.", 410);
        }
        Optional<DataSourceEntity> findByIdResult = dataSourceService.findById(entity.getDataSourceId());
        if (findByIdResult.isPresent()) {
            AlertEntity savedAlert = alertRepo.save(entity);
            setupAlert(savedAlert);
            return savedAlert;
        }
        throw new InvalidAlertException("Alert was not created because Data Source " + entity.getDataSourceId() + " does not exist.", 411);
    }

    private void validateAlert(AlertEntity entity) throws InvalidAlertException {
        if ("DISCORD".equals(entity.getType()) && !WEBHOOK_PATTERN.matcher(entity.getWebhookUrl()).matches()) {
            throw new InvalidAlertException("Alert was not added because " + entity.getWebhookUrl() + " is not a valid Discord webhook", 412);
        }
        if ("GOTIFY".equals(entity.getType()) && !WEBHOOK_PATTERN.matcher(entity.getServerUrl()).matches()) {
            throw new InvalidAlertException("Alert was not added because " + entity.getServerUrl() + " is not a valid Gotify server url", 413);
        }
        if ("GOTIFY".equals(entity.getType()) && !StringUtils.hasText(entity.getToken())) {
            throw new InvalidAlertException("Alert was not added because the Gotify API key is empty", 414);
        }
        if ("SLACK".equals(entity.getType()) && !WEBHOOK_PATTERN.matcher(entity.getWebhookUrl()).matches()) {
            throw new InvalidAlertException("Alert was not added because " + entity.getWebhookUrl() + " is not a valid Slack webhook", 412);
        }
    }

    private AlertEntity sanitize(AlertEntity entity) {
        if ("GOTIFY".equals(entity.getType())) {
            while (entity.getServerUrl().endsWith("/")) {
                entity.setServerUrl(entity.getServerUrl().substring(0, entity.getServerUrl().length() - 1));
            }
        }
        return entity;
    }

    private void setupAlert(AlertEntity savedAlert) {
        logger.info("Setting up Alert {} to match '{}'", savedAlert.getName(), savedAlert.getMatchingString());
        EventBus eventBus = dataSourceService.getEventBus(savedAlert.getDataSourceId());
        if (eventBus != null) {
            EventListener listener = null;
            if ("DISCORD".equals(savedAlert.getType())) {
                listener = new DiscordEventListener(savedAlert);
            } else if ("GOTIFY".equals(savedAlert.getType())) {
                listener = new GotifyEventListener(savedAlert);
            } else if ("SLACK".equals(savedAlert.getType())) {
                listener = new SlackEventListener(savedAlert);
            }
            if (listener == null) {
                logger.warn("Unable to setup alert '{}' because notification type is invalid. This Alert will be deleted.", savedAlert.getName());
                deleteById(savedAlert.getId());
                return;
            }
            if (savedAlert.isEnabled()) {
                eventBus.register(listener);
            }
            listeners.put(savedAlert.getId(), listener);
        } else {
            logger.warn("Unable to setup alert '{}' because Data Source does not exist. This Alert will be deleted.", savedAlert.getName());
            deleteById(savedAlert.getId());
        }
    }

    @Override
    public Iterable<AlertEntity> findAll() {
        return alertRepo.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<AlertEntity> findById = alertRepo.findById(id);
        if (findById.isPresent()) {
            alertRepo.deleteById(id);
            EventBus eventBus = dataSourceService.getEventBus(findById.get().getDataSourceId());
            if (eventBus != null && listeners.get(id) != null) {
                try {
                    eventBus.unregister(listeners.get(id));
                } catch (IllegalArgumentException ex) {
                    logger.warn("The alert '{}' was already unregistered", findById.get().getName());
                }
            }
        }
    }

    @Override
    @Transactional
    public AlertEntity setEnabled(Long id, boolean enabled) {
        Optional<AlertEntity> findById = alertRepo.findById(id);
        if (findById.isPresent()) {
            EventBus eventBus = dataSourceService.getEventBus(findById.get().getDataSourceId());
            if (eventBus != null) {
                findById.get().setEnabled(enabled);
                alertRepo.save(findById.get());
                if (!enabled) {
                    logger.info("Disabling alert '{}'", findById.get().getName());
                    try {
                        eventBus.unregister(listeners.get(id));
                    } catch (IllegalArgumentException ex) {
                        logger.warn("The alert '{}' could not be disabled because its already disabled", findById.get().getName());
                    }
                } else {
                    logger.info("Enabling alert '{}'", findById.get().getName());
                    eventBus.register(listeners.get(id));
                }
            } else {
                logger.error("Could not find event bus for data source {}", findById.get().getDataSourceId());
            }
            return findById.get();
        }
        return null;
    }

    @Override
    public void test(Long id) {
        EventListener eventListener = listeners.get(id);
        if (eventListener != null) {
            eventListener.test();
        }
    }
}

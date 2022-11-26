package com.zggis.howler.services;

import com.google.common.eventbus.EventBus;
import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.exceptions.InvalidAlertException;
import com.zggis.howler.listeners.DiscordEventListener;
import com.zggis.howler.listeners.EventListener;
import com.zggis.howler.listeners.GotifyEventListener;
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

    private static final Pattern pattern = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

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
        int result = validateAlert(newEntity);
        if (result != 200) {
            throw new InvalidAlertException("Alert failed validation", result);
        }
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

    private int validateAlert(AlertEntity entity) {
        if ("DISCORD".equals(entity.getType()) && !pattern.matcher(entity.getWebhookUrl()).matches()) {
            logger.error("Alert was not added because {} is not a valid Discord webhook", entity.getWebhookUrl());
            return 412;
        }
        if ("GOTIFY".equals(entity.getType()) && !pattern.matcher(entity.getServerUrl()).matches()) {
            logger.error("Alert was not added because {} is not a valid Gotify server url", entity.getServerUrl());
            return 413;
        }
        if ("GOTIFY".equals(entity.getType()) && !StringUtils.hasText(entity.getToken())) {
            logger.error("Alert was not added because the Gotify API key is empty");
            return 414;
        }
        return 200;
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
            if (eventBus != null) {
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

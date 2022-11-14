package com.zggis.howler.services;

import com.zggis.howler.entity.AlertEntity;

import java.util.Optional;

public interface AlertService {
    AlertEntity add(AlertEntity entity);

    Optional<AlertEntity> findById(Long id);

    Iterable<AlertEntity> findAll();

    void deleteById(Long id);
}

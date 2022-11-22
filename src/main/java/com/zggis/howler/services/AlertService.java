package com.zggis.howler.services;

import com.zggis.howler.entity.AlertEntity;

public interface AlertService {
    AlertEntity add(AlertEntity entity);

    Iterable<AlertEntity> findAll();

    void deleteById(Long id);

    AlertEntity setEnabled(Long id, boolean enabled);
}

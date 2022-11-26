package com.zggis.howler.services;

import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.exceptions.InvalidAlertException;

public interface AlertService {
    AlertEntity add(AlertEntity entity) throws InvalidAlertException;

    Iterable<AlertEntity> findAll();

    void deleteById(Long id);

    AlertEntity setEnabled(Long id, boolean enabled);

    void test(Long id);
}

package com.zggis.howler.services;

import com.google.common.eventbus.EventBus;
import com.zggis.howler.entity.DataSourceEntity;
import com.zggis.howler.exceptions.InvalidDataSourceException;

import java.util.Optional;

public interface DataSourceService {

    DataSourceEntity add(DataSourceEntity entity) throws InvalidDataSourceException;

    Optional<DataSourceEntity> findById(Long id);

    Iterable<DataSourceEntity> findAll();

    EventBus getEventBus(Long dataSourceId);

    void deleteById(Long id);
}

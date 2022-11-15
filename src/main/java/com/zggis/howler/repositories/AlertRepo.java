package com.zggis.howler.repositories;

import com.zggis.howler.entity.AlertEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface AlertRepo extends CrudRepository<AlertEntity, Long> {
    Collection<AlertEntity> deleteByDataSourceId(Long dataSourceId);
}

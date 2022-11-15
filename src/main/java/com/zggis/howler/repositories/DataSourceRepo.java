package com.zggis.howler.repositories;

import com.zggis.howler.entity.DataSourceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface DataSourceRepo extends CrudRepository<DataSourceEntity, Long> {
    Collection<DataSourceEntity> findByPath(String path);
}

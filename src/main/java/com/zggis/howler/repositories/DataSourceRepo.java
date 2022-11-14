package com.zggis.howler.repositories;

import com.zggis.howler.entity.DataSourceEntity;
import org.springframework.data.repository.CrudRepository;

public interface DataSourceRepo extends CrudRepository<DataSourceEntity, Long> {
}

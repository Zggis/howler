package com.zggis.howler.repositories;

import com.zggis.howler.entity.AlertEntity;
import com.zggis.howler.entity.DataSourceEntity;
import org.springframework.data.repository.CrudRepository;

public interface AlertRepo extends CrudRepository<AlertEntity, Long> {
}

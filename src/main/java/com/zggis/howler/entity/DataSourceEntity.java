package com.zggis.howler.entity;

import com.zggis.howler.dto.DataSourceDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DataSourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    public DataSourceEntity() {
        super();
    }

    public DataSourceEntity(DataSourceDTO dataSourceDTO) {
        this.path = dataSourceDTO.getPath();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

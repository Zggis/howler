package com.zggis.howler.entity;

import com.zggis.howler.dto.DataSourceDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class DataSourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    public DataSourceEntity(DataSourceDTO dataSourceDTO) {
        this.path = dataSourceDTO.getPath();
    }

}

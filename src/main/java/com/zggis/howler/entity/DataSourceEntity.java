package com.zggis.howler.entity;

import com.zggis.howler.dto.DataSourceDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

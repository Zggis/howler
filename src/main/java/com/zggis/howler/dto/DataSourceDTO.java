package com.zggis.howler.dto;

import com.zggis.howler.entity.DataSourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class DataSourceDTO implements Serializable {

    private static final long serialVersionUID = 3570016838112050282L;
    private Long id;
    private String path;

    public DataSourceDTO(DataSourceEntity entity) {
        this.id = entity.getId();
        this.path = entity.getPath();
    }

}

package com.zggis.howler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class LogEventDTO implements Serializable {

    private static final long serialVersionUID = 2418318656197495046L;

    private Long dataSourceId;

    private String message;

    public LogEventDTO(Long dataSourceId, String message) {
        this.dataSourceId = dataSourceId;
        this.message = message;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public String getMessage() {
        return message;
    }
}

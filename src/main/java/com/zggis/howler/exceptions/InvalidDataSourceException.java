package com.zggis.howler.exceptions;

import lombok.Getter;

@Getter
public class InvalidDataSourceException extends RuntimeException{

    private static final long serialVersionUID = 7791834997774157149L;

    private final int statusCode;

    public InvalidDataSourceException(String message, int statusCode){
        super(message);
        this.statusCode = statusCode;
    }
}

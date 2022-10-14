package com.devfortech.crudservice.exception;

import lombok.Getter;

@Getter
public class ResourceExistsException extends RuntimeException{
    private String entityName;
    private String fieldName;
    private Object fieldValue;


    public ResourceExistsException(String msg) {
        super(String.format(msg));
    }

    public ResourceExistsException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s nao encontrado com %s : '%s'", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}

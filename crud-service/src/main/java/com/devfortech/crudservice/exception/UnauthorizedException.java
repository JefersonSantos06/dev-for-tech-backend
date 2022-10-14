package com.devfortech.crudservice.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(String msg) {
        super(String.format(msg));
    }

}

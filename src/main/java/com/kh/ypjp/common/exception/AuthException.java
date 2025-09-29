package com.kh.ypjp.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthException extends RuntimeException {
    private final String errorCode;
    private String message;

    public AuthException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
        this.message = "";
    }
    
}
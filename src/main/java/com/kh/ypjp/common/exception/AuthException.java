package com.kh.ypjp.common.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String errorCode;

    public AuthException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
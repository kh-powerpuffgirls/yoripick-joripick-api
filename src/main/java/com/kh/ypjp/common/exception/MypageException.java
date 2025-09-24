package com.kh.ypjp.common.exception;

import lombok.Getter;

@Getter
public class MypageException extends RuntimeException {
    private final String errorCode;

    public MypageException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
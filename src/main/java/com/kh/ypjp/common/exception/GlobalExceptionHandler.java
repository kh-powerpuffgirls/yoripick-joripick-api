package com.kh.ypjp.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "errorCode", ex.getErrorCode()
                ));
    }
}
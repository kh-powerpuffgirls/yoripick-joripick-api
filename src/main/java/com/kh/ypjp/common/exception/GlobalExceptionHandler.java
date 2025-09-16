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
                        "errorCode", ex.getErrorCode(),
                        "message", getMessageByCode(ex.getErrorCode())
                ));
    }

    private String getMessageByCode(String code) {
        return switch (code) {
            case "WRONG_PASSWORD" -> "비밀번호가 일치하지 않습니다.";
            case "WRONG_EMAIL" -> "존재하지 않는 이메일입니다.";
            case "ACCOUNT_LOCKED" -> "계정이 잠겼습니다. 관리자에게 문의하세요.";
            default -> "로그인 중 오류가 발생했습니다.";
        };
    }
}
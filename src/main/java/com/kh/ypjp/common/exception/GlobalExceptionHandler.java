package com.kh.ypjp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("errorCode", "LARGE_FILE"));
    }
    

    @ExceptionHandler(MypageException.class)
    public ResponseEntity<?> handleMyPageException(MypageException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("errorCode", ex.getErrorCode()));
    }
}
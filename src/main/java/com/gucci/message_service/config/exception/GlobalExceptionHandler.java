package com.gucci.message_service.config.exception;

import com.gucci.common.exception.CustomException;
import com.gucci.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode()));
    }
}

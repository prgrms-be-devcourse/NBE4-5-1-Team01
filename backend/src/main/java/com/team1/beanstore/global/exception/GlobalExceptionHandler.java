package com.team1.beanstore.global.exception;

import com.team1.beanstore.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus // Spring Docs 핸들러 인식
    public ResponseEntity<RsData<Void>> ServiceExceptionHandle(ServiceException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(
                        new RsData<>(
                                e.getCode(),
                                e.getMsg()
                        )
                );
    }
}

package com.todolist.exception.handler;

import com.todolist.exception.UnauthorizedException;
import com.todolist.exception.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage());
        log.warn("UnauthorizedExceptionResponse: {}", exceptionResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }
}

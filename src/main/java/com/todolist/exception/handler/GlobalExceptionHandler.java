package com.todolist.exception.handler;

import com.todolist.exception.*;
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
        log.warn("UnauthorizedException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateEmailException(DuplicateEmailException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        log.warn("DuplicateEmailException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenAccessException(ForbiddenAccessException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.FORBIDDEN.toString(), e.getMessage());
        log.warn("ForbiddenAccessException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(TodoDetailLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> haneldTodoDetailLimitExceededException(TodoDetailLimitExceededException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        log.warn("TodoDetailLimitExceededException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.NOT_FOUND.toString(), e.getMessage());
        log.warn("NotFoundException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidDeleteException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidDeleteException(InvalidDeleteException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        log.warn("InvalidDeleteException 발생");
        log.warn("status: {}, message: {}", exceptionResponse.status(), exceptionResponse.message());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}

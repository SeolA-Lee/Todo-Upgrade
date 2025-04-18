package com.todolist.exception.dto;

public record ExceptionResponse(
        String status,
        String message
) {
}

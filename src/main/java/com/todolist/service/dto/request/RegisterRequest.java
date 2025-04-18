package com.todolist.service.dto.request;

public record RegisterRequest(
        String email,
        String password
) {
}

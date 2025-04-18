package com.todolist.service.dto.request;

public record MemberRequest(
        String email,
        String password
) {
}

package com.todolist.auth.jwt.dto;

public record JwtToken(
        String accessToken,
        String refreshToken
) {
}

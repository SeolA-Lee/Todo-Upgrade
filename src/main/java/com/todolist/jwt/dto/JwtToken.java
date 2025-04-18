package com.todolist.jwt.dto;

public record JwtToken(
        String accessToken,
        String refreshToken
) {
}

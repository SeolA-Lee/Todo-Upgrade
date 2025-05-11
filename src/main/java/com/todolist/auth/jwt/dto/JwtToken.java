package com.todolist.auth.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 토큰 생성 응답 DTO")
public record JwtToken(

        @Schema(description = "access 토큰", example = "{해당 토큰을 Authorize 'Authorization'에 넣어주세요}")
        String accessToken,

        @Schema(description = "refresh 토큰", example = "{refreshToken}")
        String refreshToken
) {
}

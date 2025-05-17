package com.todolist.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청 DTO")
public record MemberRequest(

        @Schema(description = "이메일", example = "member@gachon.ac.kr")
        String email,

        @Schema(description = "비밀번호", example = "password1234")
        String password,

        @Schema(description = "닉네임", example = "김리츠")
        String nickname
) {
}

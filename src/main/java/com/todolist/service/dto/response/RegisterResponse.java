package com.todolist.service.dto.response;

import com.todolist.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답 DTO")
public record RegisterResponse(

        @Schema(description = "회원 고유 ID", example = "1")
        Long id,

        @Schema(description = "가입한 이메일", example = "member@gachon.ac.kr")
        String email
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(member.getId(), member.getEmail());
    }
}

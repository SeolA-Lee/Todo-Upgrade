package com.todolist.service.dto.response;

import com.todolist.entity.Member;

public record RegisterResponse(
        Long id,
        String email
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(member.getId(), member.getEmail());
    }
}

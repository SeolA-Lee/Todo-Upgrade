package com.todolist.service.dto.response;

import com.todolist.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로잉 하는 유저 1명에 해당하는 응답 DTO")
public record FollowingResponse(

        @Schema(description = "회원 ID", example = "2")
        Long id,

        @Schema(description = "닉네임", example = "김가천")
        String nickname
) {
    public static FollowingResponse from(Member member) {
        return new FollowingResponse(member.getId(), member.getNickname());
    }
}

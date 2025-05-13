package com.todolist.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "팔로잉 목록 응답 DTO")
public record FollowingListResponse(

        @Schema(description = "팔로잉 목록")
        List<FollowingResponse> followings,

        @Schema(description = "요청 페이지", example = "1")
        int pageNum,

        @Schema(description = "페이지 사이즈", example = "10")
        int pageSize,

        @Schema(description = "마지막 페이지(true/false)", example = "true")
        boolean isLast
) {
    public static FollowingListResponse from(Page<FollowingResponse> followings) {
        return new FollowingListResponse(followings.getContent(), followings.getNumber(), followings.getSize(), followings.isLast());
    }
}

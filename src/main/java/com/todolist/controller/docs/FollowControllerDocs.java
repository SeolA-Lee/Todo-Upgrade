package com.todolist.controller.docs;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.dto.response.FollowingListResponse;
import com.todolist.service.dto.response.TodoListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Follow", description = "Follow 관련 API")
public interface FollowControllerDocs {

    @Operation(summary = "팔로잉 조회", description = "팔로잉 하는 유저 목록 조회를 요청합니다.")
    @ApiResponse(description = "조회 성공", responseCode = "200")
    ResponseEntity<FollowingListResponse> readFollowingList(
            CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int pageNum
    );

    @Operation(summary = "팔로우", description = "팔로우를 요청합니다.")
    @ApiResponse(description = "팔로우 성공", responseCode = "204")
    ResponseEntity<Void> follow(
            CustomUserDetails userDetails,
            @Parameter(description = "팔로우할 유저 ID", example = "2")
            @PathVariable(name = "followeeId") Long followeeId
    );

    @Operation(summary = "언팔로우", description = "언팔로우를 요청합니다.")
    @ApiResponse(description = "언팔로우 성공", responseCode = "204")
    ResponseEntity<Void> unfollow(
            CustomUserDetails userDetails,
            @Parameter(description = "언팔로우할 유저 ID", example = "2")
            @PathVariable(name = "followeeId") Long followeeId
    );

    @Operation(summary = "팔로잉 하는 유저의 투두 조회", description = "팔로잉 하는 유저의 투두 조회를 요청합니다.")
    @ApiResponse(description = "조회 성공", responseCode = "200")
    ResponseEntity<TodoListResponse> readFolloweeTodoList(
            CustomUserDetails userDetails,
            @PathVariable(name = "followeeId") Long followeeId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int pageNum
    );
}

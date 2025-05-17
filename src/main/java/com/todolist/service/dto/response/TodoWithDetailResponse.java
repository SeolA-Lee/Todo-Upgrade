package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "투두(세부 할 일 포함) 1개에 해당하는 응답 DTO")
public record TodoWithDetailResponse(

        @Schema(description = "조회수", example = "5")
        Long hit,

        @Schema(description = "(부모)투두 ID", example = "1")
        Long id,

        @Schema(description = "해야 할 일", example = "Leets 마지막 과제 하기")
        String todo,

        @Schema(description = "세부 투두 상태", example = "진행 전")
        String status,

        @Schema(description = "세부 할 일 목록")
        List<TodoDetailResponse> detailList
) {
    public static TodoWithDetailResponse from(Todo todo) {

        List<TodoDetailResponse> list = todo.getTodoDetail().stream()
                .map(TodoDetailResponse::from)
                .toList();

        return new TodoWithDetailResponse(todo.getHit(), todo.getId(), todo.getTodoList(), todo.getStatus().getKoreanStatus(), list);
    }
}

package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투두 생성 응답 DTO")
public record TodoResponse(

        @Schema(description = "투두 ID", example = "1")
        Long id,

        @Schema(description = "해야 할 일", example = "Leets 마지막 과제 하기")
        String todo,

        @Schema(description = "세부 투두 상태", example = "진행 전")
        String status
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTodoList(), todo.getStatus().getKoreanStatus());
    }
}

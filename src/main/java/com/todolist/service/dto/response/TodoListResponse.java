package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "투두 목록 응답 DTO")
public record TodoListResponse(

        @Schema(description = "투두(세부 할 일 포함) 목록")
        List<TodoWithDetailResponse> todos,

        @Schema(description = "요청 페이지", example = "1")
        int pageNum,

        @Schema(description = "페이지 사이즈", example = "5")
        int pageSize,

        @Schema(description = "마지막 페이지(true/false)", example = "true")
        boolean isLast
) {
    public static TodoListResponse from(Page<Todo> todos) {

        List<TodoWithDetailResponse> list = todos.getContent().stream()
                .map(TodoWithDetailResponse::from)
                .toList();

        return new TodoListResponse(list, todos.getNumber(), todos.getSize(), todos.isLast());
    }
}

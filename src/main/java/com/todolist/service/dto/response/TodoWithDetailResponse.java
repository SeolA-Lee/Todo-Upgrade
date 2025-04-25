package com.todolist.service.dto.response;

import com.todolist.entity.Todo;

import java.util.List;

public record TodoWithDetailResponse(
        Long id,
        String todo,
        String status,
        List<TodoDetailResponse> detailList
) {
    public static TodoWithDetailResponse from(Todo todo) {

        List<TodoDetailResponse> list = todo.getTodoDetail().stream()
                .map(TodoDetailResponse::from)
                .toList();

        return new TodoWithDetailResponse(todo.getId(), todo.getTodoList(), todo.getStatus().getKoreanStatus(), list);
    }
}

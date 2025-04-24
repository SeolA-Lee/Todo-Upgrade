package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;

import java.util.List;

public record TodoWithDetailResponse(
        Long id,
        String todo,
        TodoStatus status,
        List<TodoDetailResponse> detailList
) {
    public static TodoWithDetailResponse from(Todo todo) {

        List<TodoDetailResponse> list = todo.getTodoDetail().stream()
                .map(TodoDetailResponse::from)
                .toList();

        return new TodoWithDetailResponse(todo.getId(), todo.getTodoList(), todo.getStatus(), list);
    }
}

package com.todolist.service.dto.response;

import com.todolist.entity.Todo;

public record TodoResponse(
        Long id,
        String todo,
        String status
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTodoList(), todo.getStatus().getKoreanStatus());
    }
}

package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;

public record TodoResponse(
        Long id,
        String todo,
        TodoStatus status
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTodoList(), todo.getStatus());
    }
}

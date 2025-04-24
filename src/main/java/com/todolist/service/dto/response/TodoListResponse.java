package com.todolist.service.dto.response;

import com.todolist.entity.Todo;
import org.springframework.data.domain.Page;

import java.util.List;

public record TodoListResponse(
        List<TodoWithDetailResponse> todos,
        int pageNum,
        int pageSize,
        boolean isLast
) {
    public static TodoListResponse from(Page<Todo> todos) {

        List<TodoWithDetailResponse> list = todos.getContent().stream()
                .map(TodoWithDetailResponse::from)
                .toList();

        return new TodoListResponse(list, todos.getNumber(), todos.getSize(), todos.isLast());
    }
}

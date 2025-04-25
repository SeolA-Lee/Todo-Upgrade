package com.todolist.service.dto.request;

import com.todolist.entity.enums.TodoStatus;

public record TodoStatusUpdateRequest(
        TodoStatus status
) {
}

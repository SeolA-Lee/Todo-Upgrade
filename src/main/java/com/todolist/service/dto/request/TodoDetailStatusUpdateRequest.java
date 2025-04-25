package com.todolist.service.dto.request;

import com.todolist.entity.enums.TodoDetailStatus;

public record TodoDetailStatusUpdateRequest(
        TodoDetailStatus status
) {
}

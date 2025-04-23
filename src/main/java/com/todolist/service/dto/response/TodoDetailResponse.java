package com.todolist.service.dto.response;

import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;

public record TodoDetailResponse(
        Long parentId,
        Long id,
        String todo,
        TodoDetailStatus status
) {
    public static TodoDetailResponse from(TodoDetail todoDetail) {
        return new TodoDetailResponse(
                todoDetail.getTodo().getId(),
                todoDetail.getId(),
                todoDetail.getDetailList(),
                todoDetail.getStatus()
        );
    }
}

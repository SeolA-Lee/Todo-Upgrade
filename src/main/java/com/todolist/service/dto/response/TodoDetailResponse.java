package com.todolist.service.dto.response;

import com.todolist.entity.TodoDetail;

public record TodoDetailResponse(
        Long parentId,
        Long id,
        String todo,
        String status
) {
    public static TodoDetailResponse from(TodoDetail todoDetail) {
        return new TodoDetailResponse(
                todoDetail.getTodo().getId(),
                todoDetail.getId(),
                todoDetail.getDetailList(),
                todoDetail.getStatus().getKoreanStatus()
        );
    }
}

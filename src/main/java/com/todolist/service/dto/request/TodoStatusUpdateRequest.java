package com.todolist.service.dto.request;

import com.todolist.entity.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투두 상태 업데이트 요청 DTO")
public record TodoStatusUpdateRequest(

        @Schema(description = "투두 상태", example = "IN_PROGRESS")
        TodoStatus status
) {
}

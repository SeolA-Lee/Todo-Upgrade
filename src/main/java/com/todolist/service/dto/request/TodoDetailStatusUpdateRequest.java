package com.todolist.service.dto.request;

import com.todolist.entity.enums.TodoDetailStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세부 투두 상태 업데이트 요청 DTO")
public record TodoDetailStatusUpdateRequest(

        @Schema(description = "세부 투두 상태", example = "COMPLETED")
        TodoDetailStatus status
) {
}

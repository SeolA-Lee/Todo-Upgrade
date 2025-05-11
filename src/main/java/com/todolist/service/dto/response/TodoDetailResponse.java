package com.todolist.service.dto.response;

import com.todolist.entity.TodoDetail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세부 할 일 1개에 해당하는 응답 DTO")
public record TodoDetailResponse(

        @Schema(description = "부모 투두 ID", example = "1")
        Long parentId,

        @Schema(description = "세부 투두 ID", example = "1")
        Long id,

        @Schema(description = "세부 투두", example = "Swagger 연동하기")
        String todo,

        @Schema(description = "세부 투두 상태", example = "진행 전")
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

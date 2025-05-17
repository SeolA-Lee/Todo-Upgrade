package com.todolist.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투두 또는 세부 할 일 생성 요청 DTO")
public record TodoRequest(

        @Schema(description = "해야 할 일", example = "Leets 마지막 과제 하기")
        String todo
) {
}

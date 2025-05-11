package com.todolist.controller.docs;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.dto.request.TodoDetailStatusUpdateRequest;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TodoDetail", description = "TodoDetail API")
public interface TodoDetailControllerDocs {

    @Operation(summary = "세부 할 일 생성", description = "세부 할 일 생성을 요청합니다.")
    @ApiResponse(description = "생성 성공", responseCode = "201")
    ResponseEntity<TodoDetailResponse> createTodoDetail(
            CustomUserDetails userDetails,
            @Parameter(description = "생성할 부모 투두 ID", example = "1")
            @PathVariable(name = "todoId") Long todoId,
            TodoRequest request
    );

    @Operation(summary = "세부 할 일 상태 변경", description = "세부 할 일 진행 상태 변경을 요청합니다.")
    @ApiResponse(description = "수정 성공", responseCode = "200")
    ResponseEntity<TodoDetailResponse> updateTodoDetailStatus(
            CustomUserDetails userDetails,
            @Parameter(description = "수정할 세부 할 일 ID", example = "1")
            @PathVariable(name = "todoDetailId") Long todoDetailId,
            TodoDetailStatusUpdateRequest request
    );

    @Operation(summary = "세부 할 일 삭제", description = "세부 할 일 삭제를 요청합니다.")
    @ApiResponse(description = "삭제 성공", responseCode = "204")
    void deleteTodoDetail(
            CustomUserDetails userDetails,
            @Parameter(description = "삭제할 세부 할 일 ID들", example = "1,2,3")
            @RequestParam(name = "ids") List<Long> todoDetailIds
    );
}

package com.todolist.controller.docs;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.request.TodoStatusUpdateRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Todo", description = "Todo API")
public interface TodoControllerDocs {

    @Operation(summary = "투두 생성", description = "투두 생성을 요청합니다.")
    @ApiResponse(description = "생성 성공", responseCode = "201")
    ResponseEntity<TodoResponse> createTodo(CustomUserDetails userDetails, TodoRequest request);

    @Operation(summary = "투두 조회", description = "투두 조회를 요청합니다.")
    @ApiResponse(description = "조회 성공", responseCode = "200")
    ResponseEntity<TodoListResponse> readTodoList(
            CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int pageNum
    );

    @Operation(summary = "투두 상태 변경", description = "투두 진행 상태 변경을 요청합니다.")
    @ApiResponse(description = "수정 성공", responseCode = "200")
    ResponseEntity<TodoResponse> updateTodoStatus(
            CustomUserDetails userDetails,
            @Parameter(description = "수정할 투두 ID", example = "1")
            @PathVariable(name = "todoId") Long todoId,
            TodoStatusUpdateRequest request
    );

    @Operation(summary = "투두 삭제", description = "투두 삭제를 요청합니다.")
    @ApiResponse(description = "삭제 성공", responseCode = "204")
    void deleteTodoList(
            CustomUserDetails userDetails,
            @Parameter(description = "삭제할 투두 ID들", example = "1,2,3")
            @RequestParam(name = "ids") List<Long> todoIds
    );
}

package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.TodoDetailService;
import com.todolist.service.dto.request.TodoDetailStatusUpdateRequest;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoDetailController {

    private final TodoDetailService todoDetailService;

    @PostMapping("/{todoId}/detail")
    public ResponseEntity<TodoDetailResponse> createTodoDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "todoId") Long todoId,
            @RequestBody TodoRequest request
    ) {
        TodoDetailResponse response = todoDetailService.createTodoDetail(userDetails.member(), todoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/detail/{todoDetailId}")
    public ResponseEntity<TodoDetailResponse> updateTodoDetailStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "todoDetailId") Long todoDetailId,
            @RequestBody TodoDetailStatusUpdateRequest request
    ) {
        TodoDetailResponse response = todoDetailService.updateTodoDetailStatus(userDetails.member(), todoDetailId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/detail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "ids") List<Long> todoDetailIds
    ) {
        todoDetailService.deleteTodoDetail(userDetails.member(), todoDetailIds);
    }
}

package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.TodoService;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TodoRequest request
    ) {
        TodoResponse response = todoService.createTodo(userDetails.member(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TodoListResponse> readTodoList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int pageNum
    ) {
        TodoListResponse response = todoService.readTodoList(userDetails.member(), pageNum);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

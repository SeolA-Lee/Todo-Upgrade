package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.service.TodoService;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

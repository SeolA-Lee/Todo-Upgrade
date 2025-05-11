package com.todolist.controller;

import com.todolist.auth.user.CustomUserDetails;
import com.todolist.controller.docs.TodoControllerDocs;
import com.todolist.service.TodoService;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.request.TodoStatusUpdateRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController implements TodoControllerDocs {

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

    @PatchMapping("/{todoId}")
    public ResponseEntity<TodoResponse> updateTodoStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "todoId") Long todoId,
            @RequestBody TodoStatusUpdateRequest request
    ) {
        TodoResponse response = todoService.updateTodoStatus(userDetails.member(), todoId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "ids") List<Long> todoIds
    ) {
        todoService.deleteTodo(userDetails.member(), todoIds);
    }
}

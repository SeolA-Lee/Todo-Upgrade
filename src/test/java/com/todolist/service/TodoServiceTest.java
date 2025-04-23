package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class TodoServiceTest {

    private TodoService todoService;
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        todoService = new TodoService(todoRepository);
    }

    @Test
    @DisplayName("로그인한 회원은 TODO를 등록할 수 있으며, 한 번에 하나만 등록 가능하다.")
    void createTodo() {
        // given
        String email = "test@example.com";
        String password = "password1234";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        String todo = "오늘 할 일";
        TodoRequest request = new TodoRequest(todo);

        // when
        TodoResponse response = todoService.createTodo(member, request);

        // then
        verify(todoRepository, times(1)).save(any(Todo.class));

        assertThat(response.status()).isEqualTo(TodoStatus.NOT_STARTED);
    }
}
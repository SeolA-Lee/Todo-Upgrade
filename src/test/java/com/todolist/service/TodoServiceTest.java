package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("본인의 TODO를 조회할 수 있으며, 10개씩 세부 할 일과 함께 확인할 수 있다.")
    void readTodos() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .password("password1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        List<Todo> todoList = new ArrayList<>();

        Todo parentTodo = Todo.builder()
                .member(member)
                .todoList("할 일 1")
                .status(TodoStatus.NOT_STARTED)
                .build();

        TodoDetail todoDetail = TodoDetail.builder()
                .todo(parentTodo)
                .detailList("세부 할 일 1")
                .status(TodoDetailStatus.NOT_STARTED)
                .build();

        parentTodo.addTodoDetail(todoDetail);
        todoList.add(parentTodo);

        // 그 외 10개 더 생성
        for (int i = 0; i < 10; i++) {
            Todo todo = Todo.builder()
                    .member(member)
                    .todoList("할 일들")
                    .status(TodoStatus.NOT_STARTED)
                    .build();
            todoList.add(todo);
        }

        int pageNum = 0;

        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<Todo> todoPage = new PageImpl<>(todoList.subList(0, 10), pageable, todoList.size());

        when(todoRepository.findByMemberId(member.getId(), pageable)).thenReturn(todoPage);

        // when
        TodoListResponse response = todoService.readTodoList(member, pageNum);

        // then
        verify(todoRepository, times(1)).findByMemberId(member.getId(), pageable);

        assertThat(todoList.size()).isEqualTo(11);
        assertThat(response.todos()).hasSize(10);
        assertThat(response.isLast()).isFalse();
    }
}
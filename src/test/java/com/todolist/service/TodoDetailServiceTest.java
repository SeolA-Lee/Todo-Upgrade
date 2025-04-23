package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.NotFoundException;
import com.todolist.exception.TodoDetailLimitExceededException;
import com.todolist.repository.TodoDetailRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class TodoDetailServiceTest {

    private TodoDetailService todoDetailService;
    private TodoRepository todoRepository;
    private TodoDetailRepository todoDetailRepository;

    @BeforeEach
    void setUp() {
        todoRepository = mock(TodoRepository.class);
        todoDetailRepository = mock(TodoDetailRepository.class);
        todoDetailService = new TodoDetailService(todoRepository, todoDetailRepository);
    }

    @Test
    @DisplayName("상위 TODO에 해당하는 세부 할 일을 등록한다.")
    void createTodoDetail() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .password("password1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = Todo.builder()
                .member(member)
                .todoList("상위 할 일")
                .status(TodoStatus.NOT_STARTED)
                .build();

        Long parentTodoId = parentTodo.getId();
        when(todoRepository.findById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(todoDetailRepository.countByTodoId(parentTodoId)).thenReturn(1L);

        TodoRequest request = new TodoRequest("세부 할 일");

        // when
        TodoDetailResponse response = todoDetailService.createTodoDetail(member, parentTodoId, request);

        // then
        verify(todoRepository, times(1)).findById(parentTodoId);
        verify(todoDetailRepository, times(1)).countByTodoId(parentTodoId);
        verify(todoDetailRepository, times(1)).save(any(TodoDetail.class));

        assertThat(response.parentId()).isEqualTo(parentTodoId);
        assertThat(response.status()).isEqualTo(TodoDetailStatus.NOT_STARTED);
    }

    @Test
    @DisplayName("존재하지 않은 상위 TODO에 세부 할 일을 등록을 시도할 경우 예외가 발생한다.")
    void createTodoDetailFailedDueToParentTodoNotFound() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .password("password1234")
                .build();

        Long ghostTodoParentId = 999L;

        TodoRequest request = new TodoRequest("상위 항목이 없는 세부 할 일");

        when(todoRepository.findById(ghostTodoParentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> todoDetailService.createTodoDetail(member, ghostTodoParentId, request));
    }

    @Test
    @DisplayName("다른 사용자의 TODO에 세부 할 일 등록을 시도할 경우 예외가 발생한다.")
    void createTodoDetailFailedDueToForbidden() {
        // given
        Member other = Member.builder()
                .email("other@example.com")
                .password("password1234")
                .build();
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = Member.builder()
                .email("me@example.com")
                .password("password1234")
                .build();
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = Todo.builder()
                .member(other)
                .todoList("다른 사람의 상위 할 일")
                .status(TodoStatus.NOT_STARTED)
                .build();

        TodoRequest request = new TodoRequest("세부 할 일");

        Long parentTodoId = otherTodo.getId();
        when(todoRepository.findById(parentTodoId)).thenReturn(Optional.of(otherTodo));

        // when & then
        assertThrows(ForbiddenAccessException.class, () -> todoDetailService.createTodoDetail(me, parentTodoId, request));
    }

    @Test
    @DisplayName("세부 할 일은 최대 3개까지 등록 가능하며, 추가 등록을 시도할 경우 예외가 발생한다.")
    void createTodoDetailFailedDueToLimitExceeded() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .password("password1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = Todo.builder()
                .member(member)
                .todoList("상위 할 일")
                .status(TodoStatus.NOT_STARTED)
                .build();

        TodoRequest request = new TodoRequest("초과 세부 할 일");

        Long parentTodoId = parentTodo.getId();
        when(todoRepository.findById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(todoDetailRepository.countByTodoId(parentTodoId)).thenReturn(3L);

        // when & then
        assertThrows(TodoDetailLimitExceededException.class, () -> todoDetailService.createTodoDetail(member, parentTodoId, request));
    }
}
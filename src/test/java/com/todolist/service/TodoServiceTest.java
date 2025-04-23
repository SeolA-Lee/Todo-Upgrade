package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.NotFoundException;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class TodoServiceTest {

    private TodoService todoService;
    private MemberRepository memberRepository;
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        todoRepository = mock(TodoRepository.class);
        todoService = new TodoService(memberRepository, todoRepository);
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

        Long memberId = member.getId();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        String todo = "오늘 할 일";
        TodoRequest request = new TodoRequest(todo);

        // when
        TodoResponse response = todoService.createTodo(memberId, request);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(todoRepository, times(1)).save(any(Todo.class));

        assertThat(response.status()).isEqualTo(TodoStatus.NOT_STARTED);
    }

    @Test
    @DisplayName("존재하지 않은 사용자가 TODO를 생성할 경우 예외가 발생한다.")
    void createTodoFailedDueToMemberNotFound() {
        // given
        Long memberId = 999L;

        String todo = "오늘 할 일";
        TodoRequest request = new TodoRequest(todo);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> todoService.createTodo(memberId, request));
    }
}
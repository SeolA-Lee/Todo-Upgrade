package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.InvalidDeleteException;
import com.todolist.exception.NotFoundException;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.request.TodoStatusUpdateRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import com.todolist.service.dto.response.TodoWithDetailResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    /**
     * 등록 테스트
     */
    @Test
    @DisplayName("로그인한 회원은 TODO를 등록할 수 있으며, 한 번에 하나만 등록 가능하다.")
    void createTodo() {
        // given
        Member member = buildMember("test@example.com", "password1234");

        TodoRequest request = new TodoRequest("오늘 할 일");

        // when
        TodoResponse response = todoService.createTodo(member, request);

        // then
        verify(todoRepository, times(1)).save(any(Todo.class));

        assertThat(response.status()).isEqualTo(TodoStatus.NOT_STARTED.getKoreanStatus());
    }

    /**
     * 조회 테스트
     */
    @Test
    @DisplayName("본인의 TODO를 조회할 수 있으며, 10개씩 세부 할 일과 함께 확인할 수 있다. 나의 투두 리스트 조회 시 조회수도 확인 할 수 있다.")
    void readTodos() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        List<Todo> todoList = new ArrayList<>();

        Todo parentTodo = buildTodo(member, "할 일");
        TodoDetail todoDetail = buildTodoDetail(parentTodo, "세부 할 일");
        ReflectionTestUtils.setField(parentTodo, "hit", 1L); // 조회수가 1이라 가정

        parentTodo.addTodoDetail(todoDetail);
        todoList.add(parentTodo);

        // 그 외 10개 더 생성, 조회수는 0이라 가정
        for (int i = 0; i < 10; i++) {
            Todo todo = buildTodo(member, "할 일들");
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

        TodoWithDetailResponse parentTodoWithDetailResponse = response.todos().getFirst();
        assertThat(parentTodoWithDetailResponse.todo()).isEqualTo("할 일");
        assertThat(parentTodoWithDetailResponse.hit()).isEqualTo(1L);

        TodoWithDetailResponse otherTodoWithDetailResponse = response.todos().get(1);
        assertThat(otherTodoWithDetailResponse.todo()).isEqualTo("할 일들");
        assertThat(otherTodoWithDetailResponse.hit()).isEqualTo(0L);
    }

    /**
     * 상태 변경 테스트
     */
    @Test
    @DisplayName("본인 TODO의 진행 상태를 변경할 수 있다.")
    void updateTodoStatus() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo todo = buildTodo(member, "할 일");

        TodoStatusUpdateRequest request = new TodoStatusUpdateRequest(TodoStatus.IN_PROGRESS);

        Long todoId = todo.getId();
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.updateTodoStatus(member, todoId, request);

        // then
        verify(todoRepository, times(1)).findById(todoId);

        assertThat(response.status()).isEqualTo("진행 중");
    }

    @Test
    @DisplayName("존재하지 않는 TODO의 상태 변경을 요청할 경우 예외가 발생한다.")
    void updateTodoStatusFailedDueToTodoNotFound() {
        // given
        Member member = buildMember("test@example.com", "password1234");

        Long ghostTodoId = 999L;

        when(todoRepository.findById(ghostTodoId)).thenReturn(Optional.empty());

        TodoStatusUpdateRequest request = new TodoStatusUpdateRequest(TodoStatus.IN_PROGRESS);

        // when & then
        assertThrows(NotFoundException.class, () -> todoService.updateTodoStatus(member, ghostTodoId, request));
    }

    @Test
    @DisplayName("다른 사용자의 TODO의 상태 변경을 시도할 경우 예외가 발생한다.")
    void updateTodoStatusFailedDueToForbidden() {
        // given
        Member other = buildMember("other@example.com", "password1234");
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = buildMember("me@example.com", "password1234");
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = buildTodo(other, "다른 사람의 할 일");

        TodoStatusUpdateRequest request = new TodoStatusUpdateRequest(TodoStatus.IN_PROGRESS);

        Long todoId = otherTodo.getId();
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(otherTodo));

        // when & then
        assertThrows(ForbiddenAccessException.class, () -> todoService.updateTodoStatus(me, todoId, request));
    }

    /**
     * 삭제 테스트
     */
    @Test
    @DisplayName("본인의 완료된 TODO를 하나씩 삭제할 수 있다.")
    void deleteTodo() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo todo = buildTodo(member, "할 일");
        todo.updateStatus(TodoStatus.COMPLETED);
        ReflectionTestUtils.setField(todo, "id", 1L);

        List<Long> todoId = List.of(todo.getId());

        when(todoRepository.findAllById(todoId)).thenReturn(List.of(todo));
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        // when
        todoService.deleteTodo(member, todoId);

        // then
        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    @DisplayName("본인의 완료된 TODO 여러 개를 한번에 삭제할 수 있다.")
    void deleteTodos() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo todo1 = buildTodo(member, "할 일 1");
        Todo todo2 = buildTodo(member, "할 일 2");
        todo1.updateStatus(TodoStatus.COMPLETED);
        todo2.updateStatus(TodoStatus.COMPLETED);
        ReflectionTestUtils.setField(todo1, "id", 1L);
        ReflectionTestUtils.setField(todo2, "id", 2L);

        List<Long> todoIds = List.of(todo1.getId(), todo2.getId());

        when(todoRepository.findAllById(todoIds)).thenReturn(List.of(todo1, todo2));
        when(todoRepository.findById(todo1.getId())).thenReturn(Optional.of(todo1));
        when(todoRepository.findById(todo2.getId())).thenReturn(Optional.of(todo2));

        // when
        todoService.deleteTodo(member, todoIds);

        // then
        verify(todoRepository, times(1)).delete(todo1);
        verify(todoRepository, times(1)).delete(todo2);
    }

    @Test
    @DisplayName("존재하지 않는 TODO를 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodosFailedDueToTodoNotFound() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        List<Long> ghostTodoId = List.of(999L);

        when(todoRepository.findAllById(ghostTodoId)).thenReturn(List.of());

        // when & then
        assertThrows(NotFoundException.class, () -> todoService.deleteTodo(member, ghostTodoId));
    }

    @Test
    @DisplayName("다른 사용자의 TODO를 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodosFailedDueToForbidden() {
        // given
        Member other = buildMember("other@example.com", "password1234");
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = buildMember("me@example.com", "password1234");
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = buildTodo(other, "다른 사람의 할 일");
        ReflectionTestUtils.setField(otherTodo, "id", 1L);

        List<Long> todoIds = List.of(otherTodo.getId());

        when(todoRepository.findAllById(todoIds)).thenReturn(List.of(otherTodo));
        when(todoRepository.findById(otherTodo.getId())).thenReturn(Optional.of(otherTodo));

        // when & then
        assertThrows(ForbiddenAccessException.class, () -> todoService.deleteTodo(me, todoIds));
    }

    @Test
    @DisplayName("완료되지 않은 TODO를 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodosFailedDueToTodoIsNotCompleted() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo todo = buildTodo(member, "진행 중인 할 일");
        ReflectionTestUtils.setField(todo, "id", 1L);

        List<Long> todoIds = List.of(todo.getId());

        when(todoRepository.findAllById(todoIds)).thenReturn(List.of(todo));
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        // when & then
        assertThrows(InvalidDeleteException.class, () -> todoService.deleteTodo(member, todoIds));
    }

    /**
     * 편의 메소드
     */
    private Member buildMember(String email, String password) {
        return Member.builder()
                .email(email)
                .password(password)
                .build();
    }

    private Todo buildTodo(Member member, String content) {
        return Todo.builder()
                .member(member)
                .todoList(content)
                .status(TodoStatus.NOT_STARTED)
                .build();
    }

    private TodoDetail buildTodoDetail(Todo parentTodo, String content) {
        return TodoDetail.builder()
                .todo(parentTodo)
                .detailList(content)
                .status(TodoDetailStatus.NOT_STARTED)
                .build();
    }
}
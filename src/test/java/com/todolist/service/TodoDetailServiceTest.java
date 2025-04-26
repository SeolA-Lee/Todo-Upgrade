package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.InvalidDeleteException;
import com.todolist.exception.NotFoundException;
import com.todolist.exception.TodoDetailLimitExceededException;
import com.todolist.repository.TodoDetailRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoDetailStatusUpdateRequest;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * 등록 테스트
     */
    @Test
    @DisplayName("상위 TODO에 해당하는 세부 할 일을 등록한다.")
    void createTodoDetail() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "상위 할 일");

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
        assertThat(response.status()).isEqualTo(TodoDetailStatus.NOT_STARTED.getKoreanStatus());
    }

    @Test
    @DisplayName("존재하지 않은 상위 TODO에 세부 할 일을 등록을 시도할 경우 예외가 발생한다.")
    void createTodoDetailFailedDueToParentTodoNotFound() {
        // given
        Member member = buildMember("test@example.com", "password1234");

        Long ghostTodoParentId = 999L;

        TodoRequest request = new TodoRequest("상위 항목이 없는 세부 할 일");

        when(todoRepository.findById(ghostTodoParentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> todoDetailService.createTodoDetail(member, ghostTodoParentId, request));
    }

    /**
     * 조회 테스트
     */
    @Test
    @DisplayName("다른 사용자의 TODO에 세부 할 일 등록을 시도할 경우 예외가 발생한다.")
    void createTodoDetailFailedDueToForbidden() {
        // given
        Member other = buildMember("other@example.com", "password1234");
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = buildMember("me@example.com", "password1234");
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = buildParentTodo(other, "다른 사람의 상위 할 일");

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
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "상위 할 일");

        TodoRequest request = new TodoRequest("초과 세부 할 일");

        Long parentTodoId = parentTodo.getId();
        when(todoRepository.findById(parentTodoId)).thenReturn(Optional.of(parentTodo));
        when(todoDetailRepository.countByTodoId(parentTodoId)).thenReturn(3L);

        // when & then
        assertThrows(TodoDetailLimitExceededException.class, () -> todoDetailService.createTodoDetail(member, parentTodoId, request));
    }

    /**
     * 상태 변경 테스트
     */
    @Test
    @DisplayName("본인 TODO의 세부 할 일의 진행 상태를 변경할 수 있다.")
    void updateTodoDetailStatus() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "상위 할 일");

        TodoDetail todoDetail = buildTodoDetail(parentTodo, "하위 할 일");

        TodoDetailStatusUpdateRequest request = new TodoDetailStatusUpdateRequest(TodoDetailStatus.COMPLETED);

        when(todoDetailRepository.findById(todoDetail.getId())).thenReturn(Optional.of(todoDetail));
        when(todoRepository.findById(parentTodo.getId())).thenReturn(Optional.of(parentTodo));

        // when
        TodoDetailResponse response = todoDetailService.updateTodoDetailStatus(member, todoDetail.getId(), request);

        // then
        verify(todoDetailRepository, times(1)).findById(todoDetail.getId());
        verify(todoRepository, times(1)).findById(parentTodo.getId());

        assertThat(response.status()).isEqualTo("진행 완료");
    }

    @Test
    @DisplayName("존재하지 않는 세부 할 일의 상태 변경을 요청할 경우 예외가 발생한다.")
    void updateTodoDetailStatusFailedDueToTodoDetailNotFound() {
        // given
        Member member = buildMember("test@example.com", "password1234");

        Long ghostTodoDetailId = 999L;

        when(todoDetailRepository.findById(ghostTodoDetailId)).thenReturn(Optional.empty());

        TodoDetailStatusUpdateRequest request = new TodoDetailStatusUpdateRequest(TodoDetailStatus.COMPLETED);

        // when & then
        assertThrows(NotFoundException.class, () -> todoDetailService.updateTodoDetailStatus(member, ghostTodoDetailId, request));
    }

    @Test
    @DisplayName("다른 사용자의 세부 할 일의 상태 변경을 시도할 경우 예외가 발생한다.")
    void updateTodoDetailStatusFailedDueToForbidden() {
        // given
        Member other = buildMember("other@example.com", "password1234");
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = buildMember("me@example.com", "password1234");
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = buildParentTodo(other, "다른 사람의 할 일");

        TodoDetail otherTodoDetail = buildTodoDetail(otherTodo, "다른 사람의 하위 할 일");

        TodoDetailStatusUpdateRequest request = new TodoDetailStatusUpdateRequest(TodoDetailStatus.COMPLETED);

        Long otherTodoDetailId = otherTodoDetail.getId();
        when(todoDetailRepository.findById(otherTodoDetailId)).thenReturn(Optional.of(otherTodoDetail));
        when(todoRepository.findById(otherTodo.getId())).thenReturn(Optional.of(otherTodo));

        // when & then
        assertThrows(ForbiddenAccessException.class, () -> todoDetailService.updateTodoDetailStatus(me, otherTodoDetailId, request));
    }

    /**
     * 삭제 테스트
     */
    @Test
    @DisplayName("본인의 완료된 세부 할 일을 하나씩 삭제할 수 있다.")
    void deleteTodoDetail() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "할 일");
        ReflectionTestUtils.setField(parentTodo, "id", 1L);
        TodoDetail todoDetail = buildTodoDetail(parentTodo, "세부 할 일");
        todoDetail.updateStatus(TodoDetailStatus.COMPLETED);
        ReflectionTestUtils.setField(todoDetail, "id", 1L);

        List<Long> todoDetailId = List.of(todoDetail.getId());

        when(todoDetailRepository.findAllById(todoDetailId)).thenReturn(List.of(todoDetail));
        when(todoDetailRepository.findById(todoDetail.getId())).thenReturn(Optional.of(todoDetail));
        when(todoRepository.findById(parentTodo.getId())).thenReturn(Optional.of(parentTodo));

        // when
        todoDetailService.deleteTodoDetail(member, todoDetailId);

        // then
        verify(todoDetailRepository, times(1)).findAllById(todoDetailId);
        verify(todoDetailRepository, times(1)).delete(todoDetail);
    }

    @Test
    @DisplayName("본인의 완료된 세부 할 일 여러 개를 한번에 삭제할 수 있다.")
    void deleteTodoDetails() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "할 일");
        ReflectionTestUtils.setField(parentTodo, "id", 1L);
        TodoDetail todoDetail1 = buildTodoDetail(parentTodo, "세부 할 일 1");
        TodoDetail todoDetail2 = buildTodoDetail(parentTodo, "세부 할 일 2");
        todoDetail1.updateStatus(TodoDetailStatus.COMPLETED);
        todoDetail2.updateStatus(TodoDetailStatus.COMPLETED);
        ReflectionTestUtils.setField(todoDetail1, "id", 1L);
        ReflectionTestUtils.setField(todoDetail2, "id", 2L);

        List<Long> todoDetailIds = List.of(todoDetail1.getId(), todoDetail2.getId());

        when(todoDetailRepository.findAllById(todoDetailIds)).thenReturn(List.of(todoDetail1, todoDetail2));
        when(todoDetailRepository.findById(todoDetail1.getId())).thenReturn(Optional.of(todoDetail1));
        when(todoDetailRepository.findById(todoDetail2.getId())).thenReturn(Optional.of(todoDetail2));
        when(todoRepository.findById(parentTodo.getId())).thenReturn(Optional.of(parentTodo));

        // when
        todoDetailService.deleteTodoDetail(member, todoDetailIds);

        // then
        verify(todoDetailRepository, times(1)).findAllById(todoDetailIds);
        verify(todoDetailRepository, times(1)).delete(todoDetail1);
        verify(todoDetailRepository, times(1)).delete(todoDetail2);
    }

    @Test
    @DisplayName("존재하지 않는 세부 할 일을 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodoDetailsFailedDueToTodoDetailNotFound() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        List<Long> ghostTodoDetailId = List.of(999L);

        when(todoDetailRepository.findAllById(ghostTodoDetailId)).thenReturn(List.of());

        // when & then
        assertThrows(NotFoundException.class, () -> todoDetailService.deleteTodoDetail(member, ghostTodoDetailId));
    }


    @Test
    @DisplayName("다른 사용자의 세부 할 일을 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodoDetailsFailedDueToForbidden() {
        // given
        Member other = buildMember("other@example.com", "password1234");
        ReflectionTestUtils.setField(other, "id", 1L);

        Member me = buildMember("me@example.com", "password1234");
        ReflectionTestUtils.setField(me, "id", 2L);

        Todo otherTodo = buildParentTodo(other, "다른 사람의 할 일");
        ReflectionTestUtils.setField(otherTodo, "id", 1L);
        TodoDetail otherTodoDetail = buildTodoDetail(otherTodo, "다른 사람의 세부 할 일");
        otherTodoDetail.updateStatus(TodoDetailStatus.COMPLETED);
        ReflectionTestUtils.setField(otherTodoDetail, "id", 1L);

        List<Long> todoDetailIds = List.of(otherTodo.getId());

        when(todoDetailRepository.findAllById(todoDetailIds)).thenReturn(List.of(otherTodoDetail));
        when(todoDetailRepository.findById(otherTodoDetail.getId())).thenReturn(Optional.of(otherTodoDetail));
        when(todoRepository.findById(otherTodo.getId())).thenReturn(Optional.of(otherTodo));

        // when & then
        assertThrows(ForbiddenAccessException.class, () -> todoDetailService.deleteTodoDetail(me, todoDetailIds));
    }

    @Test
    @DisplayName("완료되지 않은 세부 할 일을 삭제 요청할 경우 예외가 발생한다.")
    void deleteTodoDetailsFailedDueToTodoDetailIsNotCompleted() {
        // given
        Member member = buildMember("test@example.com", "password1234");
        ReflectionTestUtils.setField(member, "id", 1L);

        Todo parentTodo = buildParentTodo(member, "할 일");
        ReflectionTestUtils.setField(parentTodo, "id", 1L);
        TodoDetail todoDetail = buildTodoDetail(parentTodo, "세부 할 일 1");
        ReflectionTestUtils.setField(todoDetail, "id", 1L);

        List<Long> todoDetailIds = List.of(todoDetail.getId());

        when(todoDetailRepository.findAllById(todoDetailIds)).thenReturn(List.of(todoDetail));
        when(todoDetailRepository.findById(todoDetail.getId())).thenReturn(Optional.of(todoDetail));
        when(todoRepository.findById(parentTodo.getId())).thenReturn(Optional.of(parentTodo));

        // when & then
        assertThrows(InvalidDeleteException.class, () -> todoDetailService.deleteTodoDetail(member, todoDetailIds));
    }

    /**
     * 편의 메소드
     */
    private Member buildMember(String mail, String password) {
        return Member.builder()
                .email(mail)
                .password(password)
                .build();
    }

    private Todo buildParentTodo(Member member, String content) {
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
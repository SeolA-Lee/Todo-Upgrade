package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.TodoDetail;
import com.todolist.entity.enums.TodoDetailStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.NotFoundException;
import com.todolist.exception.TodoDetailLimitExceededException;
import com.todolist.repository.TodoDetailRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoDetailStatusUpdateRequest;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoDetailService {

    private final TodoRepository todoRepository;
    private final TodoDetailRepository todoDetailRepository;

    @Transactional
    public TodoDetailResponse createTodoDetail(Member member, Long parentTodoId, TodoRequest request) {
        // 부모 투두 확인
        Todo parentTodo = todoRepository.findById(parentTodoId)
                .orElseThrow(() -> new NotFoundException("상위 할 일을 찾을 수 없습니다."));

        // 해당 사용자의 투두인지 확인
        if (!parentTodo.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("해당 목록에 접근 권한이 없습니다.");
        }

        // 해당 상위 투두의 세부 할 일 개수가 3개가 넘으면 예외
        Long count = todoDetailRepository.countByTodoId(parentTodoId);
        if (count >= 3) {
            throw new TodoDetailLimitExceededException("세부 할 일은 최대 3개까지만 추가할 수 있습니다.");
        }

        TodoDetail todoDetail = TodoDetail.builder()
                .todo(parentTodo)
                .detailList(request.todo())
                .status(TodoDetailStatus.NOT_STARTED)
                .build();
        todoDetailRepository.save(todoDetail);

        return TodoDetailResponse.from(todoDetail);
    }

    @Transactional
    public TodoDetailResponse updateTodoDetailStatus(Member member, Long todoDetailId, TodoDetailStatusUpdateRequest request) {
        // 세부 할 일 확인
        TodoDetail todoDetail = todoDetailRepository.findById(todoDetailId)
                .orElseThrow(() -> new NotFoundException("해당 할 일을 찾을 수 없습니다."));

        // 부모 투두 확인
        Todo parentTodo = todoRepository.findById(todoDetail.getTodo().getId())
                .orElseThrow(() -> new NotFoundException("상위 할 일을 찾을 수 없습니다."));

        // 해당 사용자의 투두인지 확인
        if (!parentTodo.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("해당 목록에 접근 권한이 없습니다.");
        }

        todoDetail.updateStatus(request.status());

        return TodoDetailResponse.from(todoDetail);
    }
}

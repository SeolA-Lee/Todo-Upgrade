package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.NotFoundException;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.request.TodoStatusUpdateRequest;
import com.todolist.service.dto.response.TodoListResponse;
import com.todolist.service.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse createTodo(Member member, TodoRequest request) {

        Todo todo = Todo.builder()
                .member(member)
                .todoList(request.todo())
                .status(TodoStatus.NOT_STARTED)
                .build();
        todoRepository.save(todo);

        return TodoResponse.from(todo);
    }

    public TodoListResponse readTodoList(Member member, int pageNum) {

        /**
         * 요구사항
         * - 페이지네이션을 통해 한번에 10개씩 보여진다.
         * - 조회 시 세부 할 일도 함께 조회된다.
         */
        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<Todo> todos = todoRepository.findByMemberId(member.getId(), pageable);

        return TodoListResponse.from(todos);
    }

    @Transactional
    public TodoResponse updateTodoStatus(Member member, Long todoId, TodoStatusUpdateRequest request) {
        // 투두 존재 여부
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("상위 할 일을 찾을 수 없습니다."));

        // 해당 사용자의 투두인지 확인
        if (!todo.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("해당 목록에 접근 권한이 없습니다.");
        }

        todo.updateStatus(request.status());

        return TodoResponse.from(todo);
    }
}

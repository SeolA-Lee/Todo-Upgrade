package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
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
}

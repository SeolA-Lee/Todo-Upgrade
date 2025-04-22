package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.NotFoundException;
import com.todolist.repository.MemberRepository;
import com.todolist.repository.TodoRepository;
import com.todolist.service.dto.request.TodoRequest;
import com.todolist.service.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse createTodo(Long memberId, TodoRequest request) {
        // 사용자 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Todo todo = Todo.builder()
                .member(member)
                .todoList(request.todo())
                .status(TodoStatus.NOT_STARTED)
                .build();
        todoRepository.save(todo);

        return TodoResponse.from(todo);
    }
}

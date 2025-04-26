package com.todolist.service;

import com.todolist.entity.Member;
import com.todolist.entity.Todo;
import com.todolist.entity.enums.TodoStatus;
import com.todolist.exception.ForbiddenAccessException;
import com.todolist.exception.InvalidDeleteException;
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

import java.util.List;

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

        Todo todo = findTodo(todoId);
        verifyTodoOwner(member, todo);

        todo.updateStatus(request.status());

        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(Member member, List<Long> todoIds) {

        /**
         * 요구사항
         * - 사용자는 투두를 완료 시 개별로 삭제할 수 있다.
         * - 사용자는 여러 개의 투두를 한 번에 선택하여 삭제할 수 있다.
         * - 상위 투두를 삭제할 시 하위 할 일과 함께 삭제된다.
         */
        List<Todo> todoList = todoRepository.findAllById(todoIds);

        // 요청한 개수와 조회한 개수가 일치하는지 확인
        if (todoList.size() != todoIds.size()) {
            throw new NotFoundException("존재하지 않는 TODO가 있습니다.");
        }

        for (Todo todo : todoList) {
            Todo findTodo = findTodo(todo.getId());
            verifyTodoOwner(member, findTodo);
            verifyTodoIsCompleted(findTodo);

            todoRepository.delete(findTodo);
        }
    }

    // 투두 존재 여부 확인하는 메소드
    private Todo findTodo(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("해당 TODO를 찾을 수 없습니다: todo_id = " + todoId));
    }

    // 해당 사용자의 투두인지 확인하는 메소드
    private void verifyTodoOwner(Member member, Todo todo) {
        if (!todo.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("해당 목록에 접근 권한이 없습니다: todo_id = " + todo.getId());
        }
    }

    // 완료된 투두인지 확인하는 메소드
    private void verifyTodoIsCompleted(Todo todo) {
        if (todo.getStatus() != TodoStatus.COMPLETED) {
            throw new InvalidDeleteException("완료된 TODO만 삭제 가능합니다: todo_id = " + todo.getId() + ", status = " + todo.getStatus());
        }
    }
}

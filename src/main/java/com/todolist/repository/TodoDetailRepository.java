package com.todolist.repository;

import com.todolist.entity.TodoDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoDetailRepository extends JpaRepository<TodoDetail, Long> {
}

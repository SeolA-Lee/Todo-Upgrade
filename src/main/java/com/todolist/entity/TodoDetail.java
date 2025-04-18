package com.todolist.entity;

import com.todolist.entity.enums.TodoDetailStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TodoDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Column(nullable = false)
    private String detailList;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoDetailStatus status = TodoDetailStatus.NOT_STARTED;

    @Builder
    public TodoDetail(Todo todo, String detailList, TodoDetailStatus status) {
        this.todo = todo;
        this.detailList = detailList;
        this.status = status;
    }
}

package com.todolist.entity;

import com.todolist.entity.enums.TodoDetailStatus;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(PROTECTED)
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

    public void updateStatus(TodoDetailStatus status) {
        this.status = status;
    }
}

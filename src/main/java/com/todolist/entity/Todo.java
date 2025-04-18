package com.todolist.entity;

import com.todolist.entity.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String todoList;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status = TodoStatus.NOT_STARTED;

    @Builder
    public Todo(Member member, String todoList, TodoStatus status) {
        this.member = member;
        this.todoList = todoList;
        this.status = status;
    }
}

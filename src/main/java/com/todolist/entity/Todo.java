package com.todolist.entity;

import com.todolist.entity.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String todoList;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status = TodoStatus.NOT_STARTED;

    @Builder.Default
    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TodoDetail> todoDetail = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Long hit = 0L;

    public void addTodoDetail(TodoDetail todoDetail) {
        this.todoDetail.add(todoDetail);
        todoDetail.setTodo(this);
    }

    public void updateStatus(TodoStatus status) {
        this.status = status;
    }
}

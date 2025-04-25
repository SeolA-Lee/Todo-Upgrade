package com.todolist.entity.enums;

public enum TodoStatus {
    // 투두리스트는 진행 전, 진행 중, 진행 완료로 표시
    NOT_STARTED("진행 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("진행 완료");

    private final String status;

    TodoStatus(String status) {
        this.status = status;
    }

    public String getKoreanStatus() {
        return status;
    }
}

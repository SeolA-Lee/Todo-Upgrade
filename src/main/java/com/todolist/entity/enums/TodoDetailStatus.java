package com.todolist.entity.enums;

public enum TodoDetailStatus {
    // 세부 할 일 목록은 진행 전, 진행 완료로만 표시
    NOT_STARTED("진행 전"),
    COMPLETED("진행 완료");

    private final String status;

    TodoDetailStatus(String status) {
        this.status = status;
    }

    public String getKoreanStatus() {
        return status;
    }
}

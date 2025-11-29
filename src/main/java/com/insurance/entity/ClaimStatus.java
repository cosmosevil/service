package com.insurance.entity;

public enum ClaimStatus {
    OPEN("Открыта"),
    UNDER_REVIEW("На рассмотрении"),
    APPROVED("Одобрена"),
    REJECTED("Отклонена"),
    PAID("Выплачена");

    private final String description;

    ClaimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
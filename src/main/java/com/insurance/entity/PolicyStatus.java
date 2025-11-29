package com.insurance.entity;

public enum PolicyStatus {
    ACTIVE("Активен"),
    EXPIRED("Истёк"),
    CANCELLED("Отменен");

    private final String description;

    PolicyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
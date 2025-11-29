package com.insurance.entity;

public enum PolicyType {
    AUTO("Автомобильное страхование"),
    HEALTH("Страхование здоровья"),
    PROPERTY("Страхование имущества"),
    TRAVEL("Туристическое страхование"),
    LIABILITY("Страхование ответственности");

    private final String description;

    PolicyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package com.BTL.SupportDecision.Enum;

public enum MealTime {
    BREAKFAST("Bữa sáng"),
    LUNCH("Bữa trưa"),
    AFTERNOON("Bữa chiều"),
    DINNER("Bữa tối");

    private String value;

    MealTime(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

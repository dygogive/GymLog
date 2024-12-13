package com.example.gymlog.data;

public enum Motion {
    PRESS_BY_ARMS("press with arms"),
    PULL_BY_ARMS("pull with arms"),
    PRESS_BY_LEGS("press with legs"),
    PULL_BY_LEGS("pull with legs");

    private final String description;

    Motion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

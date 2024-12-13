package com.example.gymlog.data.exercise;

public enum Motion {

    //ОСНОВНІ РУХИ ПРИ ВИКОНАННІ ВПРАВ
    PRESS_BY_ARMS("press with arms"),
    PULL_BY_ARMS("pull with arms"),
    PRESS_BY_LEGS("press with legs"),
    PULL_BY_LEGS("pull with legs");

    //ОПИС
    private final String description;

    //конструктор створнення екземпляра руху
    Motion(String description) {
        this.description = description;
    }

    //метод видає опис
    public String getDescription() {
        return description;
    }
}

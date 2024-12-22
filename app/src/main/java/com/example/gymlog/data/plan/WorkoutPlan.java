package com.example.gymlog.data.plan;

import java.util.List;

public class WorkoutPlan {
    private int id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private List<WorkoutDay> days; // Список тренувальних днів

    // Конструктори
    public WorkoutPlan() {}

    public WorkoutPlan(int id, String name, List<WorkoutDay> days) {
        this.id = id;
        this.name = name;
        this.days = days;
    }

    // Геттери та сеттери
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkoutDay> getDays() {
        return days;
    }

    public void setDays(List<WorkoutDay> days) {
        this.days = days;
    }
}
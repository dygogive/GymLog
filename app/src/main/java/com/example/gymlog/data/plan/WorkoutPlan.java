package com.example.gymlog.data.plan;

import java.util.List;

public class WorkoutPlan {
    private long id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private String description;
    private List<WorkoutDay> days; // Список тренувальних днів

    // Конструктори
    public WorkoutPlan() {}

    public WorkoutPlan(long id, String name, String description, List<WorkoutDay> days) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.description = description;
    }


}
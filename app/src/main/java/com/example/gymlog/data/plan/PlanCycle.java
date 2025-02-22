package com.example.gymlog.data.plan;

import java.util.List;

public class PlanCycle {
    private long id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private String description; //опис
    private List<GymDay> gymDays; // Список тренувальних днів

    // Конструктори
    public PlanCycle() {}

    public PlanCycle(long id, String name, String description, List<GymDay> gymDays) {
        this.id = id;
        this.name = name;
        this.gymDays = gymDays;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
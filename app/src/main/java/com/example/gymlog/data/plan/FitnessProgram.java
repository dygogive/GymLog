package com.example.gymlog.data.plan;

import java.util.List;

public class FitnessProgram implements BasePlanItem {
    private long id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private String description; //опис
    private List<GymSession> gymSessions; // Список тренувальних днів

    // Конструктори
    public FitnessProgram(long id, String name, String description, List<GymSession> gymSessions) {
        this.id = id;
        this.name = name;
        this.gymSessions = gymSessions;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
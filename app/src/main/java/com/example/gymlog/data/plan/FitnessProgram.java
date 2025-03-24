package com.example.gymlog.data.plan;

import java.util.List;

public class FitnessProgram implements BasePlanItem {
    private long id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private String description; //опис
    private List<GymSession> gymSessions; // Список тренувальних днів

    private int position = 0;


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

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public int getPosition() {
        return position;
    }

    public List<GymSession> getGymSessions() {
        return gymSessions;
    }

    public void setGymSessions(List<GymSession> gymSessions) {
        this.gymSessions = gymSessions;
    }
}
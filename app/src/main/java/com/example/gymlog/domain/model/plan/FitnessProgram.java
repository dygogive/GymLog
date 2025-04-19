package com.example.gymlog.domain.model.plan;

import java.util.List;

public class FitnessProgram implements BasePlanItem {
    private long id; // Унікальний ідентифікатор програми
    private String name; // Назва програми
    private String description; //опис

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public int getIs_active() {
        return is_active;
    }

    private String creation_date; //дата створення
    private List<GymSession> gymSessions; // Список тренувальних днів

    private int position = 0;

    private int is_active = 1;


    // Конструктори
    public FitnessProgram(long id, String name, String description, String creation_date, int position, int is_active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creation_date = creation_date;
        this.position = position;
        this.is_active = is_active;
    }

    public FitnessProgram(long id, String name, String description, List<GymSession> gymSessions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creation_date = "///";
        this.gymSessions = gymSessions;
        this.position = -1;
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
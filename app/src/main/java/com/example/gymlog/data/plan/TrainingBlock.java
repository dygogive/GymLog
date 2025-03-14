package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Exercise;

import java.util.List;

public class TrainingBlock {
    private long id; // Унікальний ідентифікатор групи
    private long gymDayId; // Ідентифікатор дня тренування
    private String name; // Назва групи
    private String description; // Опис або мета групи
    private List<Exercise> exercises; // Список вправ


    // Конструктори
    public TrainingBlock() {}

    public TrainingBlock(long id, long gymDayId, String name, String description, List<Exercise> exercises) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exercises = exercises;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getGymDayId() {
        return gymDayId;
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

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }


    public List<Exercise> getExercises() {
        return exercises;
    }

}
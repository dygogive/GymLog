package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Exercise;

import java.util.ArrayList;
import java.util.List;

public class TrainingBlock {
    private long id; // Унікальний ідентифікатор групи
    private long gymDayId; // Ідентифікатор дня тренування
    private String name; // Назва групи
    private String description; // Опис або мета групи
    private List<Exercise> exercises; // Список вправ
    private int position = 0; // За замовчуванням


    // Конструктори
    public TrainingBlock(long id, long gymDayId, String name, String description) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<Exercise>();
    }

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

    public void setId(long id) {
        this.id = id;
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



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
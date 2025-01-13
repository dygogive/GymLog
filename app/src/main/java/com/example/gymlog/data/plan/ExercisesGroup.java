package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Exercise;

import java.util.List;

public class ExercisesGroup {
    private long id; // Унікальний ідентифікатор групи
    private long workoutDayId; // Ідентифікатор дня тренування
    private String name; // Назва групи
    private String description; // Опис або мета групи
    private List<Exercise> exercises; // Список вправ

    // Конструктори
    public ExercisesGroup() {}

    public ExercisesGroup(long id, long workoutDayId, String name, String description, List<Exercise> exercises) {
        this.id = id;
        this.workoutDayId = workoutDayId;
        this.name = name;
        this.description = description;
        this.exercises = exercises;
    }



}
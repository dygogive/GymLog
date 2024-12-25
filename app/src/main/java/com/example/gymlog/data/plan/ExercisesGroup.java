package com.example.gymlog.data.plan;

import java.util.List;

public class ExercisesGroup {
    private long id; // Унікальний ідентифікатор групи
    private long workoutDayId; // Ідентифікатор дня тренування
    private String name; // Назва групи
    private String metadata; // Опис або мета групи
    private List<WorkoutExercise> workoutExercises; // Список вправ

    // Конструктори
    public ExercisesGroup() {}

    public ExercisesGroup(long id, long workoutDayId, String name, String metadata, List<WorkoutExercise> workoutExercises) {
        this.id = id;
        this.workoutDayId = workoutDayId;
        this.name = name;
        this.metadata = metadata;
        this.workoutExercises = workoutExercises;
    }

    // Геттери та сеттери
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getWorkoutDayId() {
        return workoutDayId;
    }

    public void setWorkoutDayId(int workoutDayId) {
        this.workoutDayId = workoutDayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public List<WorkoutExercise> getWorkoutExercises() {
        return workoutExercises;
    }

    public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }
}
package com.example.gymlog.data.plan;

import java.util.List;

public class WorkoutDay {
    private int id; // Унікальний ідентифікатор дня
    private int planId; // Ідентифікатор програми, до якої належить день
    private String date; // Дата тренування
    private List<WorkoutExercise> exercises; // Список вправ

    // Конструктори
    public WorkoutDay() {}

    public WorkoutDay(int id, int planId, String date, List<WorkoutExercise> exercises) {
        this.id = id;
        this.planId = planId;
        this.date = date;
        this.exercises = exercises;
    }

    // Геттери та сеттери
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }
}

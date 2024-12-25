package com.example.gymlog.data.plan;

import java.time.LocalDate;
import java.util.List;

public class WorkoutDay {
    private int id;
    private int planId;
    private LocalDate date; // Тип змінено на LocalDate
    private List<ExercisesGroup> exercisesGroups;

    public WorkoutDay(int id, int planId, LocalDate date, List<ExercisesGroup> exercisesGroups) {
        this.id = id;
        this.planId = planId;
        this.date = date;
        this.exercisesGroups = exercisesGroups;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getPlanId() {
        return planId;
    }

    public List<ExercisesGroup> getExercisesGroups() {
        return exercisesGroups;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public void setExercisesGroups(List<ExercisesGroup> exercisesGroups) {
        this.exercisesGroups = exercisesGroups;
    }
}



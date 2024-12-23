package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.set.WorkoutSet;

import java.util.List;

public class WorkoutExercise {
    private int id; // Унікальний ідентифікатор вправи у програмі
    private int goalId; // Ідентифікатор цілі тренування
    private int exerciseId; // Ідентифікатор базової вправи
    private Exercise exercise; // Об'єкт Exercise для опису
    private List<WorkoutSet> sets; // Список підходів

    // Конструктори
    public WorkoutExercise() {}

    public WorkoutExercise(int id, int goalId, int exerciseId, Exercise exercise, List<WorkoutSet> sets) {
        this.id = id;
        this.goalId = goalId;
        this.exerciseId = exerciseId;
        this.exercise = exercise;
        this.sets = sets;
    }

    // Геттери та сеттери
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }
}

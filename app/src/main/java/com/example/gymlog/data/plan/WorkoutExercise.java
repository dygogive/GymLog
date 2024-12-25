package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.set.WorkoutSet;

import java.util.List;

public class WorkoutExercise {
    private Exercise exercise; // Базова вправа
    private int exercisesGroupId;
    private List<WorkoutSet> sets;

    public WorkoutExercise(Exercise exercise, int exercisesGroupId, List<WorkoutSet> sets) {
        this.exercise = exercise;
        this.exercisesGroupId = exercisesGroupId;
        this.sets = sets;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    // Інші геттери та сеттери

}


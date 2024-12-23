package com.example.gymlog.data.plan;

import java.util.List;

public class ExercisesGroup {
    private int id; // Унікальний ідентифікатор цілі
    private int workoutGoalId; // Ідентифікатор програми, до якої належить день
    private String name; // Назва
    private String description; // Назва
    private List<WorkoutExercise> workoutExercises; // Список вправ

    // Конструктори
    public ExercisesGroup() {}

    public ExercisesGroup(int id, int workoutGoalId, String name,
                       String description, List<WorkoutExercise> workoutExercises) {
        this.id = id;
        this.workoutGoalId = workoutGoalId;
        this.name = name;
        this.workoutExercises = workoutExercises;
    }
}

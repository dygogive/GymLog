package com.example.gymlog.data.plan;

import java.util.List;

public class WorkoutGoal {
    private int id; // Унікальний ідентифікатор цілі
    private int workoutDayId; // Ідентифікатор програми, до якої належить день
    private String name; // Назва
    private String description; // Назва
    private List<ExercisesGroup> exercisesGroups; // Список вправ

    // Конструктори
    public WorkoutGoal() {}

    public WorkoutGoal(int id, int workoutDayId, String name,
                       String description, List<ExercisesGroup> exercisesGroups) {
        this.id = id;
        this.workoutDayId = workoutDayId;
        this.name = name;
        this.exercisesGroups = exercisesGroups;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getWorkoutDayId() {
        return workoutDayId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWorkoutDayId(int workoutDayId) {
        this.workoutDayId = workoutDayId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ExercisesGroup> getExercisesGroups() {
        return exercisesGroups;
    }

    public void setExercisesGroups(List<ExercisesGroup> exercisesGroups) {
        this.exercisesGroups = exercisesGroups;
    }
}

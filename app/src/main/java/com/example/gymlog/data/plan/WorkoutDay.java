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




}



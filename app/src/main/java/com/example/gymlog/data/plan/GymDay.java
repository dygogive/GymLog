package com.example.gymlog.data.plan;

import java.util.List;

public class GymDay {
    private int id;
    private int planId;
    private List<ExercisesGroup> exercisesGroups;


    public GymDay(int id, int planId, List<ExercisesGroup> exercisesGroups) {
        this.id = id;
        this.planId = planId;
        this.exercisesGroups = exercisesGroups;
    }


    public List<ExercisesGroup> getExercisesGroups() {
        return exercisesGroups;
    }
}



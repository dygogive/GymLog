package com.example.gymlog.data.exercise;

import java.util.List;

//вправи, що в списках для тренувальних блоків
public class ExerciseInBlock extends Exercise {
    private int position;

    public ExerciseInBlock(long id, String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment, int position) {
        super(id, name, motion, muscleGroups, equipment);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}



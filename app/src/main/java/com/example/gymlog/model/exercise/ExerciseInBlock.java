package com.example.gymlog.model.exercise;

import android.content.Context;

import java.util.List;

//вправи, що в списках для тренувальних блоків
public class ExerciseInBlock extends Exercise {
    private long linkId; // id з таблиці TrainingBlockExercises
    private int position;

    public ExerciseInBlock(long linkId, long exerciseId, String name, String description, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment, int position) {
        super(exerciseId, name, description, motion, muscleGroups, equipment);
        this.linkId = linkId;
        this.position = position;
    }

    public long getLinkId() { return linkId; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }


}




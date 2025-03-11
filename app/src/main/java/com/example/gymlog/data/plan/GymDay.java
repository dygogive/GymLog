package com.example.gymlog.data.plan;

import java.util.List;

public class GymDay {
    private int id;
    private int planId;

    private String name = " ";

    private List<TrainingBlock> trainingBlocks;

    private String description;


    public GymDay(int id, int planId, List<TrainingBlock> trainingBlocks) {
        this.id = id;
        this.planId = planId;
        this.trainingBlocks = trainingBlocks;
    }


    public List<TrainingBlock> getExercisesGroups() {
        return trainingBlocks;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String dayName) {
        name = dayName;
    }
}



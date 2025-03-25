package com.example.gymlog.model.plan;

import java.util.List;

public class GymSession implements BasePlanItem {
    private long id;
    private int planId;

    private int position;

    public void setId(long id) {
        this.id = id;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPlanId() {
        return planId;
    }

    public int getPosition() {
        return position;
    }

    private List<TrainingBlock> trainingBlocks;

    private String name = " ";

    private String description;


    public GymSession(int id, int fitProgramId, List<TrainingBlock> trainingBlocks) {
        this.id = id;
        this.planId = fitProgramId;
        this.trainingBlocks = trainingBlocks;
    }

    public GymSession(int id, int fitProgramId, String name, String description, List<TrainingBlock> trainingBlocks) {
        this.id = id;
        this.planId = fitProgramId;
        this.name = name;
        this.description = description;
        this.trainingBlocks = trainingBlocks;
    }

    public GymSession(int id, int fitProgramId, String name, String description, int position, List<TrainingBlock> trainingBlocks) {
        this.id = id;
        this.planId = fitProgramId;
        this.name = name;
        this.description = description;
        this.position = position;
        this.trainingBlocks = trainingBlocks;
    }


    public long getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public List<TrainingBlock> getTrainingBlocks() {
        return trainingBlocks;
    }

    public void setTrainingBlocks(List<TrainingBlock> trainingBlocks) {
        this.trainingBlocks = trainingBlocks;
    }
}



package com.example.gymlog.data.plan;

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

    private String name = " ";

    private String description;


    public GymSession(int id, int fitProgramId, List<TrainingBlock> trainingBlocks) {
        this.id = id;
        this.planId = fitProgramId;
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
}



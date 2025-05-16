package com.example.gymlog.domain.model.legacy.plan;

import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion;
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup;

import java.util.ArrayList;
import java.util.List;

public class TrainingBlock {
    private long id;
    private long gymDayId;




    public void setExerciseInBlocks(List<ExerciseInBlock> exerciseInBlocks) {
        this.exerciseInBlocks = exerciseInBlocks;
    }

    private String uuid = "nope";
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getUuid() {
        return uuid;
    }


    public List<ExerciseInBlock> getExerciseInBlocks() {
        return exerciseInBlocks;
    }

    private String name;
    private String description;
    private List<ExerciseInBlock> exerciseInBlocks;

    private List<Motion> motions; // фільтри
    private List<MuscleGroup> muscleGroupList;
    private List<Equipment> equipmentList; // фільтри

    private int position = 0;

    // Конструктори

    public TrainingBlock(long id, long gymDayId, String name, String description) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exerciseInBlocks = new ArrayList<>();
        this.motions = new ArrayList<>();
        this.muscleGroupList = new ArrayList<>();
        this.equipmentList = new ArrayList<>();
    }

    public TrainingBlock(long id, long gymDayId, String name, String description, List<ExerciseInBlock> exerciseInBlocks) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exerciseInBlocks = exerciseInBlocks != null ? exerciseInBlocks : new ArrayList<>();
        this.motions = new ArrayList<>();
        this.muscleGroupList = new ArrayList<>();
        this.equipmentList = new ArrayList<>();
    }

    public TrainingBlock(long id, long gymDayId, String name, String description,
                         List<Motion> motions, List<MuscleGroup> muscleGroupList,
                         List<Equipment> equipmentList, int position,
                         List<ExerciseInBlock> exerciseInBlocks) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.motions = motions != null ? motions : new ArrayList<>();
        this.muscleGroupList = muscleGroupList != null ? muscleGroupList : new ArrayList<>();
        this.equipmentList = equipmentList != null ? equipmentList : new ArrayList<>();
        this.position = position;
        this.exerciseInBlocks = exerciseInBlocks != null ? exerciseInBlocks : new ArrayList<>();
    }

    // Геттери
    public long getId() { return id; }
    public long getGymDayId() { return gymDayId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPosition() { return position; }
    public List<ExerciseInBlock> getExercises() { return exerciseInBlocks; }
    public List<Motion> getMotions() { return motions; }
    public List<MuscleGroup> getMuscleGroupList() { return muscleGroupList; }
    public List<Equipment> getEquipmentList() { return equipmentList; }

    // Сеттери
    public void setId(long id) { this.id = id; }
    public void setGymDayId(long gymDayId) { this.gymDayId = gymDayId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPosition(int position) { this.position = position; }
    public void setExercises(List<ExerciseInBlock> exerciseInBlocks) { this.exerciseInBlocks = exerciseInBlocks; }
    public void setMotions(List<Motion> motions) { this.motions = motions; }
    public void setMuscleGroupList(List<MuscleGroup> muscleGroupList) { this.muscleGroupList = muscleGroupList; }
    public void setEquipmentList(List<Equipment> equipmentList) { this.equipmentList = equipmentList; }
}

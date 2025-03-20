package com.example.gymlog.data.plan;

import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.ArrayList;
import java.util.List;

public class TrainingBlock {
    private long id; // Унікальний ідентифікатор групи
    private long gymDayId; // Ідентифікатор дня тренування
    private String name; // Назва групи
    private String description; // Опис або мета групи
    private List<Exercise> exercises; // Список вправ

    private Motion motion; //фільтр вправ
    private List<MuscleGroup> muscleGroupList; //фільтр вправ
    private Equipment equipment; //фільтр вправ

    private int position = 0; // За замовчуванням


    // Конструктори
    public TrainingBlock(long id, long gymDayId, String name, String description) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<Exercise>();
    }

    public TrainingBlock(long id, long gymDayId, String name, String description, List<Exercise> exercises) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.exercises = exercises;
    }

    public TrainingBlock(long id, long gymDayId, String name, String description, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment, int position, List<Exercise> exercises) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
        this.position = position;
        this.exercises = exercises;
    }
    public TrainingBlock(long id, long gymDayId, String name, String description, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment, int position) {
        this.id = id;
        this.gymDayId = gymDayId;
        this.name = name;
        this.description = description;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
        this.position = position;
    }



    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getGymDayId() {
        return gymDayId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }


    public List<Exercise> getExercises() {
        return exercises;
    }



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Motion getMotion() {
        return motion;
    }

    public List<MuscleGroup> getMuscleGroupList() {
        return muscleGroupList;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setGymDayId(long gymDayId) {
        this.gymDayId = gymDayId;
    }

    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    public void setMuscleGroupList(List<MuscleGroup> muscleGroupList) {
        this.muscleGroupList = muscleGroupList;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

}
package com.example.gymlog.data.exercise;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

//test comit user1
public class Exercise {

    private long id = -1;
    private String name;
    private Motion motion;
    private List<MuscleGroup> muscleGroupList;
    private Equipment equipment;
    private boolean isCustom = false;


    public Exercise(Long id, String name, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.id = id;
        this.name = name;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return name;
    }

    // Гетери та сетери
    public String getName() {
        return name;
    }

    public long getId() {
        return id;
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

    public boolean getIsCustom(){
        return isCustom;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setCustom(boolean b) {
        isCustom = b;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setMotion(Motion newMotion) {
        motion = newMotion;
    }

    public void setMuscleGroupList(List<MuscleGroup> newMuscleGroups) {
        muscleGroupList = newMuscleGroups;
    }

    public void setEquipment(Equipment newEquipment) {
        equipment = newEquipment;
    }
}

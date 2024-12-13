package com.example.gymlog.data.exercise;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

//test comit user1
public class Exercise {

    private String name;
    private Motion motion;
    private List<MuscleGroup> muscleGroupList;
    private Equipment equipment;

    public Exercise(String name, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
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

    public Motion getMotion() {
        return motion;
    }

    public List<MuscleGroup> getMuscleGroupList() {
        return muscleGroupList;
    }

    public Equipment getEquipment() {
        return equipment;
    }

}

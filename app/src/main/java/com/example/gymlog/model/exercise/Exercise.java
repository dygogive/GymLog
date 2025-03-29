package com.example.gymlog.model.exercise;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gymlog.R;

import java.lang.reflect.Field;
import java.util.List;

//test comit user1
public class Exercise {

    private long id;
    private String name;
    private String description;

    private Motion motion;
    private List<MuscleGroup> muscleGroupList;
    private Equipment equipment;
    private boolean isCustom = false;
    private boolean expanded = false;
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Exercise(Long id, String name, String description, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
    }

    @NonNull
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

    public String getNameOnly(Context context) {
        int resId;
        String value = getName();
        if(!isCustom){
            try {
                Field field = R.string.class.getDeclaredField(name);
                resId = field.getInt(null);
                value = context.getString(resId);
            } catch (Exception e) {
                Log.e("ResourceError", "Ресурс не знайдено для " + name);
            }
            return value;
        } else return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

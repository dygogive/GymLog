package com.example.gymlog.model.exercise;


import android.content.Context;
import android.util.Log;

import com.example.gymlog.R;

import java.lang.reflect.Field;
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

    public String getNameOnly(Context context) {
        int resId = -1;
        String value = getName();
        if(!isCustom){
            try {
                Field field = R.string.class.getDeclaredField(name);
                resId = field.getInt(null);
                value = context.getString(resId);
            } catch (Exception e) {
                Log.e("ResourceError", "Ресурс не знайдено для " + name);
                resId = 0;
            }
            return value;
        } else return name;
    }
}

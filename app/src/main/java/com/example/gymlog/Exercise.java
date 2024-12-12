package com.example.gymlog;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//test comit master
public class Exercise {

    private String name;
    private Motion motion; // Оновлений Motion
    private List<MuscleGroup> muscleGroupList;
    private Equipment equipment;

    public Exercise(String name, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.name = name;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    // define of motion - press or pull
    public enum Motion {
        PRESS_BY_ARMS("press with arms"),
        PULL_BY_ARMS("pull with arms"),
        PRESS_BY_LEGS("press with legs"),
        PULL_BY_LEGS("pull with legs");

        private final String description;

        Motion(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }



    //define muscles
    public enum MuscleGroup {
        CHEST_LOWER(R.string.muscle_chest_lower),
        CHEST_UPPER(R.string.muscle_chest_upper),
        CHEST_FULL(R.string.muscle_chest_full),

        TRICEPS(R.string.muscle_triceps),
        BICEPS_ARMS(R.string.muscle_biceps_arms),

        TRAPS_UPPER(R.string.muscle_traps_upper),
        TRAPS_MIDDLE(R.string.muscle_traps_middle),
        TRAPS_LOWER(R.string.muscle_traps_lower),
        LATS(R.string.muscle_lats),

        HAMSTRINGS(R.string.muscle_hamstrings),
        QUADRICEPS(R.string.muscle_quadriceps),
        GLUTES(R.string.muscle_glutes),

        DELTS_REAR(R.string.muscle_delts_rear),
        DELTS_SIDE(R.string.muscle_delts_side),
        DELTS_FRONT(R.string.muscle_delts_front),

        LONGISSIMUS(R.string.muscle_longissimus);

        private final int descriptionResId;

        MuscleGroup(int descriptionResId) {
            this.descriptionResId = descriptionResId;
        }

        public int getDescriptionResId() {
            return descriptionResId;
        }

        public String getDescription(Context context) {
            return context.getString(descriptionResId);
        }

        //test co
        public static List<Exercise> getExercisesByMuscle(MuscleGroup muscleGroup){
            List <Exercise> exerciseList = ExercisesData.getExercisesData();
            List<Exercise> sortedExeList = new ArrayList<>();
            for(Exercise exercise : exerciseList) {
                if(exercise.muscleGroupList.contains(muscleGroup)){
                    sortedExeList.add(exercise);
                }
            }
            return sortedExeList;
        }
    }


    //define equipment for sets
    public enum Equipment {
        BARBELL, DUMBBELLS, BODY_WEIGHT, TRAINER;
    }
}

package com.example.gymlog.data.exercise;


import java.util.ArrayList;
import java.util.List;

//test comit user1
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

package com.example.gymlog.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.gymlog.data.Exercise.*;

public class ExercisesData {

    public static List<Exercise> getExercisesData() {

        //Get BASIC exercises 87
        List<Exercise> exerciseList = new ArrayList<>();

        exerciseList.add( new Exercise(
                "Жим лежачи в наклоні",
                Motion.PRESS_BY_ARMS,
                Arrays.asList(MuscleGroup.CHEST_UPPER,MuscleGroup.TRICEPS),
                Equipment.DUMBBELLS
        ));

        exerciseList.add( new Exercise(
                "Жим на брусах",
                Motion.PRESS_BY_ARMS,
                Arrays.asList(MuscleGroup.CHEST_LOWER,MuscleGroup.TRICEPS),
                Equipment.BODY_WEIGHT
        ));

        exerciseList.add( new Exercise(
                "Тяга в наклоні",
                Motion.PULL_BY_ARMS,
                Arrays.asList(MuscleGroup.TRAPS_LOWER,MuscleGroup.TRAPS_MIDDLE, MuscleGroup.BICEPS_ARMS),
                Equipment.DUMBBELLS
        ));

        exerciseList.add( new Exercise(
                "Підтягування",
                Motion.PULL_BY_ARMS,
                Arrays.asList(MuscleGroup.LATS,MuscleGroup.BICEPS_ARMS),
                Equipment.BODY_WEIGHT
        ));

        exerciseList.add( new Exercise(
                "Присідання",
                Motion.PRESS_BY_LEGS,
                Arrays.asList(MuscleGroup.QUADRICEPS,MuscleGroup.GLUTES,MuscleGroup.LONGISSIMUS),
                Equipment.BARBELL
        ));

        exerciseList.add( new Exercise(
                "Мертва тяга",
                Motion.PULL_BY_LEGS,
                Arrays.asList(MuscleGroup.HAMSTRINGS,MuscleGroup.LONGISSIMUS),
                Equipment.BARBELL
        ));

        return exerciseList;
    }

    public static List<String> getExeNames(){
        List<String> namesExercises = new ArrayList<>(getExercisesData().size());

        for (Exercise exercise : getExercisesData()) {
            namesExercises.add(exercise.getName());
        }

        return namesExercises;
    }




}

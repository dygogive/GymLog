package com.example.gymlog.ui.exercise2.factories;


import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultExercisesFactory {

    public static List<Exercise> getExercisesForAttribute(ExerciseDAO exerciseDAO, AttributeType attributeType, String attribute) {
        // Отримуємо список вправ за атрибутом через ExerciseDAO
        return exerciseDAO.getExercisesByAttribute(attributeType, attribute);
    }


    public static List<String> getNamesExercisesForAttribute(ExerciseDAO exerciseDAO, AttributeType attributeType, String attribute) {
        // Отримуємо список вправ за атрибутом через ExerciseDAO
        List<Exercise> exercises = getExercisesForAttribute(exerciseDAO, attributeType, attribute);

        // Перетворюємо список Exercise на список назв вправ
        List<String> exerciseNames = new ArrayList<>();
        for (Exercise exercise : exercises) {
            exerciseNames.add(exercise.getName());
        }

        return exerciseNames;
    }





    // Метод для створення базових вправ
    public static void initializeDefaultExercises(ExerciseDAO exerciseDAO) {


        if(exerciseDAO.getAllExercises().isEmpty()) {

            exerciseDAO.addExercise(
                    "exercise_dip_weighted", // Жим на брусах
                    Motion.PRESS_DOWNWARDS,
                    Arrays.asList(MuscleGroup.CHEST_LOWER, MuscleGroup.TRICEPS, MuscleGroup.CHEST),
                    Equipment.KETTLEBELL,
                    false
            );


            exerciseDAO.addExercise(
                    "exercise_incline_db_press",  // жим гантелей на наклонній лаві
                    Motion.PRESS_UP_MIDDLE,
                    Arrays.asList(MuscleGroup.CHEST_UPPER, MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_pullup_weighted", //підтягування
                    Motion.PULL_UPWARDS,
                    Arrays.asList(MuscleGroup.LATS, MuscleGroup.BICEPS),
                    Equipment.KETTLEBELL,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_db_row", //тяга гантелей в наклоні
                    Motion.PULL_DOWN_MIDDLE,
                    Arrays.asList(MuscleGroup.TRAPS_MIDDLE, MuscleGroup.BICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_squat_barbell", //присідання
                    Motion.PRESS_BY_LEGS,
                    Arrays.asList(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_deadlift", //мертва тяга
                    Motion.PULL_BY_LEGS,
                    Arrays.asList(MuscleGroup.HAMSTRINGS, MuscleGroup.TRAPS_LOWER, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );
        }
        
    }

}
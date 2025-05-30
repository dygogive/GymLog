package com.example.gymlog.data.local.legacy;


import android.util.Log;

import com.example.gymlog.domain.model.legacy.attribute.AttributesForFilterExercises;
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.legacy.exercise.Exercise;
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion;
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup;

import java.util.Arrays;
import java.util.List;

public class DefaultExercisesFactory {

    public static List<Exercise> getExercisesForAttribute(ExerciseDAO exerciseDAO, AttributesForFilterExercises attributesForFilterExercises, String attribute) {
        // Отримуємо список вправ за атрибутом через ExerciseDAO
        return exerciseDAO.getExercisesByAttribute(attributesForFilterExercises, attribute);
    }




    // Метод для створення базових вправ
    public static void initializeDefaultExercises(ExerciseDAO exerciseDAO) {


        if(exerciseDAO.getAllExercises().isEmpty()) {

            exerciseDAO.addExercise(
                    "exercise_dip_weighted", // Жим на брусах
                    "",
                    Motion.PRESS_DOWNWARDS,
                    Arrays.asList(MuscleGroup.CHEST_LOWER, MuscleGroup.TRICEPS, MuscleGroup.CHEST),
                    Equipment.KETTLEBELL,
                    false
            );


            exerciseDAO.addExercise(
                    "exercise_incline_db_press",  // жим гантелей на наклонній лаві
                    "",
                    Motion.PRESS_UP_MIDDLE,
                    Arrays.asList(MuscleGroup.CHEST_UPPER, MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_pullup_weighted", //підтягування
                    "",
                    Motion.PULL_UPWARDS,
                    Arrays.asList(MuscleGroup.LATS, MuscleGroup.BICEPS),
                    Equipment.KETTLEBELL,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_db_row", //тяга гантелей в наклоні
                    "",
                    Motion.PULL_DOWN_MIDDLE,
                    Arrays.asList(MuscleGroup.TRAPS_MIDDLE, MuscleGroup.BICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_squat_barbell", //присідання
                    "",
                    Motion.PRESS_BY_LEGS,
                    Arrays.asList(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );


            Log.d("initializeDefaultExercises", "initializeDefaultExercises");
            exerciseDAO.addExercise(
                    "exercise_deadlift", //мертва тяга
                    "",
                    Motion.PULL_BY_LEGS,
                    Arrays.asList(MuscleGroup.HAMSTRINGS, MuscleGroup.TRAPS_LOWER, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );
        }

    }


}
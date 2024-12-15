package com.example.gymlog.ui.exercise2.factories;


import android.content.Context;
import android.util.Log;

import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExerciseFactory {

    public static List<String> getExercisesForAttribute(AttributeType attributeType, String attribute) {

        switch (attributeType) {
            case EQUIPMENT:
                switch (attribute) {
                    case "BARBELL":
                        return Arrays.asList("Dumbbell Press", "Dumbbell Row");
                    case "DUMBBELLS":
                        return Arrays.asList("Barbell Squat", "Barbell Deadlift");
                    default:
                        return Collections.emptyList();
                }
            case MUSCLE_GROUP:
                switch (attribute) {
                    case "CHEST_UPPER":
                        return Arrays.asList("Bench Press", "Incline Press");
                    case "TRAPS_LOWER":
                        return Arrays.asList("Pull-Up", "Deadlift");
                    default:
                        return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }




    // Метод для створення базових вправ
    public static void initializeDefaultExercises(Context context, ExerciseDAO exerciseDAO) {


        if(exerciseDAO.getAllExercises(context).isEmpty()) {

            exerciseDAO.addExercise(
                    "exercise_dip_weighted", // Отримуємо назву з ресурсів
                    Motion.PRESS_BY_ARMS,
                    Arrays.asList(MuscleGroup.CHEST_LOWER, MuscleGroup.TRICEPS),
                    Equipment.WEIGHT,
                    false
            );


            exerciseDAO.addExercise(
                    "exercise_incline_db_press",
                    Motion.PRESS_BY_ARMS,
                    Arrays.asList(MuscleGroup.CHEST_UPPER, MuscleGroup.TRICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_pullup_weighted",
                    Motion.PULL_BY_ARMS,
                    Arrays.asList(MuscleGroup.LATS, MuscleGroup.BICEPS),
                    Equipment.WEIGHT,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_db_row",
                    Motion.PULL_BY_ARMS,
                    Arrays.asList(MuscleGroup.TRAPS_MIDDLE, MuscleGroup.BICEPS),
                    Equipment.DUMBBELLS,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_squat_barbell",
                    Motion.PRESS_BY_LEGS,
                    Arrays.asList(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );

            exerciseDAO.addExercise(
                    "exercise_deadlift",
                    Motion.PULL_BY_LEGS,
                    Arrays.asList(MuscleGroup.HAMSTRINGS, MuscleGroup.TRAPS_LOWER, MuscleGroup.LONGISSIMUS),
                    Equipment.BARBELL,
                    false
            );
        }
        
    }

}
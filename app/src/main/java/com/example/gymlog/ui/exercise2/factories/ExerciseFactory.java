package com.example.gymlog.ui.exercise2.factories;


import android.util.Log;

import com.example.gymlog.data.exercise.AttributeType;
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
}
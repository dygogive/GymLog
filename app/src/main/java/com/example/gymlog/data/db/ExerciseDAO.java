package com.example.gymlog.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseDAO {
    private SQLiteDatabase database;

    public ExerciseDAO(SQLiteDatabase database) {
        this.database = database;
    }

    // Додати вправу
    public long addExercise(String id, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
        ContentValues values = new ContentValues();
        values.put("name", id); // Зберігаємо ідентифікатор з ресурсів
        values.put("motion", motion.name());
        values.put("muscleGroups", TextUtils.join(",", muscleGroups.stream().map(Enum::name).toArray(String[]::new)));
        values.put("equipment", equipment.name());
        return database.insert("Exercise", null, values);
    }


    // Отримати всі вправи
    public List<Exercise> getAllExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        Cursor cursor = database.query("Exercise", null, null, null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion"))); // Перетворення String у Motion
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment"))); // Перетворення String у Equipment

                // Перетворення muscleGroups із рядка на список
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                for (String muscle : muscleGroupsString.split(",")) {
                    muscleGroups.add(MuscleGroup.valueOf(muscle)); // Перетворення кожного String у MuscleGroup
                }

                exerciseList.add(new Exercise(name, motion, muscleGroups, equipment));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }



    public List<Exercise> getExercisesByMuscle(MuscleGroup muscleGroup) {
        List<Exercise> exerciseList = new ArrayList<>();
        Cursor cursor = database.query("Exercise", null, null, null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment")));

                // Перетворення muscleGroups із рядка на список
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                for (String muscle : muscleGroupsString.split(",")) {
                    muscleGroups.add(MuscleGroup.valueOf(muscle));
                }

                // Додати вправу, якщо вона містить потрібну групу м'язів
                if (muscleGroups.contains(muscleGroup)) {
                    exerciseList.add(new Exercise(name, motion, muscleGroups, equipment));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }



}

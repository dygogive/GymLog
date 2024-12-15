package com.example.gymlog.data.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    Context context;

    public ExerciseDAO(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        database = dbHelper.getReadableDatabase();
    }


    // Додати вправу
    public void addExercise(String exerciseName, Motion motion, List<MuscleGroup> muscleGroups,
                            Equipment equipment, boolean isCustom) {
        ContentValues values = new ContentValues();
        values.put("name", exerciseName);
        values.put("motion", motion.name());
        values.put("muscleGroups", TextUtils.join(",", muscleGroups.stream().map(Enum::name).toArray(String[]::new)));
        values.put("equipment", equipment.name());
        values.put("isCustom", isCustom ? 1 : 0);

        database.insert("Exercise", null, values);
    }


    //отримати курсор
    public Cursor getCursor(){
        Cursor cursor = database.query("Exercise", null, null, null, null, null, null);
        return cursor;
    }



    // Отримати всі вправи
    public List<Exercise> getAllExercises(Context context) {
        List<Exercise> exerciseList = new ArrayList<>();
        Cursor cursor = database.query("Exercise", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                boolean isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1;
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment")));

                List<MuscleGroup> muscleGroups = new ArrayList<>();
                for (String muscle : muscleGroupsString.split(",")) {
                    muscleGroups.add(MuscleGroup.valueOf(muscle));
                }
                if(!isCustom) {
                    int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
                    name = context.getString(resId);
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


    // Отримуємо вправи за атрибутом
    public List<Exercise> getExercisesByAttribute(AttributeType attributeType, String attribute) {
        List<Exercise> exercises = new ArrayList<>();
        String query;

        // Визначаємо запит на основі типу атрибуту
        switch (attributeType) {
            case EQUIPMENT:
                query = "SELECT * FROM Exercise WHERE equipment = ?";
                break;
            case MUSCLE_GROUP:
                query = "SELECT * FROM Exercise WHERE muscleGroups LIKE ?";
                attribute = "%" + attribute + "%"; // Додаємо символи для пошуку підрядка
                break;
            default:
                return exercises; // Порожній список, якщо тип атрибуту невідомий
        }

        // Виконуємо запит
        Cursor cursor = database.rawQuery(query, new String[]{attribute});
        if (cursor.moveToFirst()) {
            do {
                // Створення об'єкта Exercise
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment")));

                // Перетворюємо muscleGroupsString у список
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                for (String group : muscleGroupsString.split(",")) {
                    muscleGroups.add(MuscleGroup.valueOf(group.trim()));
                }

                // Додаємо вправу до списку
                exercises.add(new Exercise(name, motion, muscleGroups, equipment));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return exercises;
    }



    // Логування всіх вправ у таблиці
    public void allExercisesInLog(Context context) {
        List<Exercise> exercises = getAllExercises(context);
        for (Exercise exercise : exercises) {
            Log.d("ExerciseLog", "Name: " + exercise.getName() + "---" +
                    "Motion: " + exercise.getMotion() + "---" +
                    "Equipment: " + exercise.getEquipment() + "---" +
                    "Muscle Groups: " + exercise.getMuscleGroupList());
        }
    }

}

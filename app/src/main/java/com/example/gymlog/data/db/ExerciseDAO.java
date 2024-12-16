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
import java.util.stream.Collectors;

public class ExerciseDAO {
    private final SQLiteDatabase database;
    private final Context context;

    public ExerciseDAO(Context context) {
        this.context = context;
        this.database = new DBHelper(context).getReadableDatabase();
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


    // Метод для оновлення вправи
    public boolean updateExercise(Exercise exercise) {

        ContentValues values = new ContentValues();
        values.put("name", exercise.getName());
        values.put("motion", exercise.getMotion().name());
        values.put("muscleGroups", TextUtils.join(",", exercise.getMuscleGroupList()
                .stream()
                .map(Enum::name)
                .toArray(String[]::new)));
        values.put("equipment", exercise.getEquipment().name());
        values.put("isCustom", 1); // Позначаємо як кастомну вправу



        // Оновлюємо запис у таблиці за старою назвою
        int rowsAffected = database.update(
                "Exercise",
                values,
                "id = ?",
                new String[]{String.valueOf(exercise.getId())}
        );


        return rowsAffected > 0; // Повертає true, якщо запис було оновлено
    }


    // Отримати всі вправи
    public List<Exercise> getAllExercises() {
        return getExercisesFromCursor(database.query("Exercise", null, null, null, null, null, null));
    }

    // Отримати вправи за атрибутом
    public List<Exercise> getExercisesByAttribute(AttributeType attributeType, String attribute) {
        String query;
        switch (attributeType) {
            case EQUIPMENT:
                query = "SELECT * FROM Exercise WHERE equipment = ?";
                break;
            case MOTION:
                query = "SELECT * FROM Exercise WHERE motion = ?";
                break;
            case MUSCLE_GROUP:
                query = "SELECT * FROM Exercise WHERE muscleGroups LIKE ?";
                attribute = "%" + attribute + "%";
                break;
            default:
                return new ArrayList<>();
        }
        return getExercisesFromCursor(database.rawQuery(query, new String[]{attribute}));
    }

    // Отримати вправи за типом м'язів
    public List<Exercise> getExercisesByMuscle(MuscleGroup muscleGroup) {
        return getExercisesByAttribute(AttributeType.MUSCLE_GROUP, muscleGroup.name());
    }

    // Логування всіх вправ у таблиці
    public void logAllExercises() {
        for (Exercise exercise : getAllExercises()) {
            Log.d("ExerciseLog", "Name: " + exercise.getName() + "---" +
                    "Motion: " + exercise.getMotion() + "---" +
                    "Equipment: " + exercise.getEquipment() + "---" +
                    "Muscle Groups: " + exercise.getMuscleGroupList());
        }
    }

    // Універсальний метод для отримання вправ із курсора
    private List<Exercise> getExercisesFromCursor(Cursor cursor) {
        List<Exercise> exerciseList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                boolean isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1;
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment")));

                List<MuscleGroup> muscleGroups = muscleGroupsString == null || muscleGroupsString.isEmpty() ?
                        new ArrayList<>() :
                        List.of(muscleGroupsString.split(",")).stream().map(MuscleGroup::valueOf).collect(Collectors.toList());

                if (!isCustom) {
                    int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
                    name = resId != 0 ? context.getString(resId) : name;
                }

                exerciseList.add(new Exercise(id, name, motion, muscleGroups, equipment));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exerciseList;
    }

}

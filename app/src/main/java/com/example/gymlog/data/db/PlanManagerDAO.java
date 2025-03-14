package com.example.gymlog.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.plan.FitnessProgram;
import com.example.gymlog.data.plan.GymSession;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.List;

public class PlanManagerDAO {
    private final DBHelper dbHelper;
    private final Context context;


    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    // Додаємо новий план у базу
    public long addFitProgram(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", fitnessProgram.getName());
        values.put("description", fitnessProgram.getDescription());
        values.put("creation_date", System.currentTimeMillis());
        values.put("is_active", 0);

        long id = db.insert("PlanCycles", null, values);
        db.close();
        return id;
    }

    // Отримуємо всі плани з бази
    public List<FitnessProgram> getAllPlans() {
        List<FitnessProgram> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles", null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                List<GymSession> gymSessions = getGymDaysByPlanId(id);
                plans.add(new FitnessProgram(id, name, description, gymSessions));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    // Отримуємо план за ID
    public FitnessProgram getPlanById(long planId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        FitnessProgram fitnessProgram = null;

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles WHERE id = ?", new String[]{String.valueOf(planId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            List<GymSession> gymSessions = getGymDaysByPlanId(planId);
            fitnessProgram = new FitnessProgram(planId, name, description, gymSessions);
        }
        cursor.close();
        db.close();
        return fitnessProgram;
    }


    // Отримуємо список тренувальних днів для плану
// Отримуємо список тренувальних днів для плану
    public List<GymSession> getGymDaysByPlanId(long planId) {
        List<GymSession> gymSessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, day_name, description FROM GymDays WHERE plan_id = ?",
                new String[]{String.valueOf(planId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String dayName = cursor.getString(cursor.getColumnIndex("day_name"));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                List<TrainingBlock> trainingBlocks = getTrainingBlocksByDayId(id);

                GymSession gymSession = new GymSession(id, (int) planId, trainingBlocks);
                gymSession.setName(dayName); // Якщо є метод setName()
                gymSession.setDescription(description); // Якщо є метод setDescription()

                gymSessions.add(gymSession);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return gymSessions;
    }



    // Отримуємо всі тренувальні блоки для конкретного дня
    public List<TrainingBlock> getExerciseGroupsByDayId(int gymDayId) {
        List<TrainingBlock> trainingBlocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description FROM ExercisesGroups WHERE gym_day_id = ?",
                new String[]{String.valueOf(gymDayId)}
        );

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);

                trainingBlocks.add(new TrainingBlock(id, gymDayId, name, description, new ArrayList<>()));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trainingBlocks;
    }


    // Оновлюємо день(сесію) тренування у базі даних
    public void updateGymSession(GymSession gymSession) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day_name", gymSession.getName());
        values.put("description", gymSession.getDescription());

        db.update("GymDays", values, "id = ?", new String[]{String.valueOf(gymSession.getId())});
        db.close();
    }

    // Видаляємо Сесію жиму
    public void deleteGymSession(long gymSessionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "id = ?", new String[]{String.valueOf(gymSessionId)});
        db.close();
    }



    // Оновлюємо план у базі даних
    public void updatePlan(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", fitnessProgram.getName());
        values.put("description", fitnessProgram.getDescription());

        db.update("PlanCycles", values, "id = ?", new String[]{String.valueOf(fitnessProgram.getId())});
        db.close();
    }

    // Видаляємо план
    public void deletePlan(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("PlanCycles", "id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }


    // Додаємо новий тренувальний день
    public long addGymDay(long planId, String dayName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("plan_id", planId);
        values.put("day_name", dayName);
        values.put("description", description);

        long gymDayId = db.insert("GymDays", null, values);
        db.close();
        return gymDayId;
    }





    // Видаляємо всі дні плану (для заміни оновленим списком)
    public void deleteGymDaysByPlanId(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "plan_id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }

    // Додаємо список днів у план
    public void addGymDays(long planId, List<GymSession> gymSessions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < gymSessions.size(); i++) {
            GymSession day = gymSessions.get(i);
            ContentValues values = new ContentValues();
            values.put("plan_id", planId);
            values.put("day_name", i + 1);
            values.put("description", day.getDescription());

            long dayId = db.insert("GymDays", null, values);
            // тут можна оновлювати також TrainingBlocks
        }

        db.close();
    }
















    // Додаємо тренувальний блок
    public long addTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("gym_day_id", block.getGymDayId());
        values.put("name", block.getName());
        values.put("description", block.getDescription());

        long id = db.insert("TrainingBlock", null, values);
        db.close();
        return id;
    }

    // Отримуємо всі тренувальні блоки для дня
    public List<TrainingBlock> getTrainingBlocksByDayId(long gymDayId) {
        List<TrainingBlock> blocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM TrainingBlock WHERE gym_day_id = ?",
                new String[]{String.valueOf(gymDayId)});
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                blocks.add(new TrainingBlock(id, gymDayId, name, description, new ArrayList<>()));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return blocks;
    }

    // Додаємо фільтр до тренувального блоку (motion, muscle, equipment)
    public void addTrainingBlockFilter(long trainingBlockId, String filterType, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trainingBlockId", trainingBlockId);
        values.put(filterType, value);

        String tableName = "";
        if (filterType.equals("motionType")) tableName = "TrainingBlockMotion";
        if (filterType.equals("muscleGroup")) tableName = "TrainingBlockMuscleGroup";
        if (filterType.equals("equipment")) tableName = "TrainingBlockEquipment";

        db.insert(tableName, null, values);
        db.close();
    }

    // Видаляємо тренувальний блок
    public void deleteTrainingBlock(long blockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockEquipment", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlock", "id = ?", new String[]{String.valueOf(blockId)});
        db.close();
    }



    // Оновлюємо тренувальний блок у базі
    public void updateTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", block.getName());
        values.put("description", block.getDescription());

        db.update("TrainingBlock", values, "id = ?", new String[]{String.valueOf(block.getId())});
        db.close();
    }








    // Отримуємо список вправ для тренувального блоку за фільтрами
    // Отримуємо список вправ для тренувального блоку за фільтрами
    public List<Exercise> getExercisesForTrainingBlock(long trainingBlockId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Для зручності додаємо логування запиту
        String query = "SELECT DISTINCT e.id, e.name, e.motion, e.muscleGroups, e.equipment, e.isCustom " +
                "FROM Exercise e " +
                // Зв'язуємо Exercise.motion з TrainingBlockMotion.motionType
                "LEFT JOIN TrainingBlockMotion tm ON e.motion = tm.motionType " +
                // Для muscleGroups використовуємо LIKE, адже можуть бути кілька груп
                "LEFT JOIN TrainingBlockMuscleGroup tmg ON ',' || e.muscleGroups || ',' LIKE '%,' || tmg.muscleGroup || ',%' " +
                // Зв'язуємо Exercise.equipment з TrainingBlockEquipment.equipment
                "LEFT JOIN TrainingBlockEquipment te ON e.equipment = te.equipment " +
                // Має співпадати хоч один фільтр
                "WHERE tm.trainingBlockId = ? OR tmg.trainingBlockId = ? OR te.trainingBlockId = ?";

        Log.d("DB_DEBUG_SQL", "Executing SQL for Block ID: " + trainingBlockId);
        Log.d("DB_DEBUG_SQL", "SQL Query: " + query);

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(trainingBlockId),
                String.valueOf(trainingBlockId),
                String.valueOf(trainingBlockId)
        });

        if (cursor.moveToFirst()) {
            do {
                // Зчитуємо основні поля
                @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String exerciseName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String motionStr = cursor.getString(cursor.getColumnIndex("motion"));
                @SuppressLint("Range") String muscleGroupsStr = cursor.getString(cursor.getColumnIndex("muscleGroups"));
                @SuppressLint("Range") String equipmentStr = cursor.getString(cursor.getColumnIndex("equipment"));

                Log.d("DB_DEBUG_CURSOR", "Exercise: " + exerciseName +
                        " | motion=" + motionStr +
                        " | muscleGroups=" + muscleGroupsStr +
                        " | equipment=" + equipmentStr);

                // 1) Конвертуємо motionStr -> Motion (ENUM)
                Motion motion = null;
                if (motionStr != null && !motionStr.isEmpty()) {
                    try {
                        motion = Motion.valueOf(motionStr);
                    } catch (IllegalArgumentException e) {
                        // Якщо в БД лишився старий локалізований текст
                        Log.e("DB_DEBUG_CURSOR", "Unknown Motion enum name: " + motionStr, e);
                    }
                }

                // 2) Конвертуємо equipmentStr -> Equipment (ENUM)
                Equipment equipment = null;
                if (equipmentStr != null && !equipmentStr.isEmpty()) {
                    try {
                        equipment = Equipment.valueOf(equipmentStr);
                    } catch (IllegalArgumentException e) {
                        Log.e("DB_DEBUG_CURSOR", "Unknown Equipment enum name: " + equipmentStr, e);
                    }
                }

                // 3) Конвертуємо muscleGroupsStr -> List<MuscleGroup>
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                if (muscleGroupsStr != null && !muscleGroupsStr.isEmpty()) {
                    // Припускаємо, що зберігається як 'CHEST_LOWER,CHEST_UPPER'
                    String[] mgArray = muscleGroupsStr.split(",");
                    for (String mg : mgArray) {
                        mg = mg.trim();
                        try {
                            MuscleGroup mgEnum = MuscleGroup.valueOf(mg);
                            muscleGroups.add(mgEnum);
                        } catch (IllegalArgumentException e) {
                            Log.e("DB_DEBUG_CURSOR", "Unknown MuscleGroup enum name: " + mg, e);
                        }
                    }
                }

                // Створюємо об'єкт Exercise
                Exercise exercise = new Exercise(id, exerciseName, motion, muscleGroups, equipment);
                exercises.add(exercise);

            } while (cursor.moveToNext());
        } else {
            Log.d("DB_DEBUG_EXERCISES", "No exercises found for Block ID: " + trainingBlockId);
        }

        cursor.close();
        db.close();
        return exercises;
    }










    // Отримує список фільтрів для тренувального блоку за категорією (motionType, muscleGroup, equipment)
    public List<String> getTrainingBlockFilters(long trainingBlockId, String filterType) {
        List<String> filters = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String tableName = "";
        if (filterType.equals("motionType")) tableName = "TrainingBlockMotion";
        if (filterType.equals("muscleGroup")) tableName = "TrainingBlockMuscleGroup";
        if (filterType.equals("equipment")) tableName = "TrainingBlockEquipment";

        Cursor cursor = db.rawQuery(
                "SELECT " + filterType + " FROM " + tableName + " WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String value = cursor.getString(cursor.getColumnIndex(filterType));
                filters.add(value);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return filters;
    }





    // Видаляє всі фільтри тренувального блоку перед оновленням
    public void clearTrainingBlockFilters(long trainingBlockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockEquipment", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.close();
    }


    // Додаткові методи для завантаження днів і блоків...
}
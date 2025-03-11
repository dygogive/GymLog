package com.example.gymlog.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymlog.data.plan.PlanCycle;
import com.example.gymlog.data.plan.GymDay;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.List;

public class PlanManagerDAO {
    private final DBHelper dbHelper;

    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // Додаємо новий план у базу
    public long addPlan(PlanCycle planCycle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", planCycle.getName());
        values.put("description", planCycle.getDescription());
        values.put("creation_date", System.currentTimeMillis());
        values.put("is_active", 0);

        long id = db.insert("PlanCycles", null, values);
        db.close();
        return id;
    }

    // Отримуємо всі плани з бази
    public List<PlanCycle> getAllPlans() {
        List<PlanCycle> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles", null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                List<GymDay> gymDays = getGymDaysByPlanId(id);
                plans.add(new PlanCycle(id, name, description, gymDays));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    // Отримуємо план за ID
    public PlanCycle getPlanById(long planId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        PlanCycle planCycle = null;

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles WHERE id = ?", new String[]{String.valueOf(planId)});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            List<GymDay> gymDays = getGymDaysByPlanId(planId);
            planCycle = new PlanCycle(planId, name, description, gymDays);
        }
        cursor.close();
        db.close();
        return planCycle;
    }


    // Отримуємо список тренувальних днів для плану
    public List<GymDay> getGymDaysByPlanId(long planId) {
        List<GymDay> gymDays = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM GymDays WHERE plan_id = ?",
                new String[]{String.valueOf(planId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                List<TrainingBlock> trainingBlocks = getExerciseGroupsByDayId(id);
                gymDays.add(new GymDay(id, (int) planId, trainingBlocks));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gymDays;
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


    // Оновлюємо план у базі даних
    public void updatePlan(PlanCycle planCycle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", planCycle.getName());
        values.put("description", planCycle.getDescription());

        db.update("PlanCycles", values, "id = ?", new String[]{String.valueOf(planCycle.getId())});
        db.close();
    }

    // Видаляємо план
    public void deletePlan(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("PlanCycles", "id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }


    // Додаємо новий тренувальний день
    public long addGymDay(long planId, int dayOrder, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("plan_id", planId);
        values.put("day_order", dayOrder);
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
    public void addGymDays(long planId, List<GymDay> gymDays) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < gymDays.size(); i++) {
            GymDay day = gymDays.get(i);
            ContentValues values = new ContentValues();
            values.put("plan_id", planId);
            values.put("day_order", i + 1);
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




    // Додаткові методи для завантаження днів і блоків...
}
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

/**
 * DAO-клас для управління планами (FitnessProgram), днями (GymSession) і блоками (TrainingBlock).
 * Охоплює:
 *  - створення / оновлення / видалення планів і їхніх дочірніх сутностей
 *  - отримання списків
 *  - роботу з позиціями (position) та фільтрами (TrainingBlockMotion, ...).
 */
public class PlanManagerDAO {

    private final DBHelper dbHelper;
    private final Context context;

    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context  = context;
    }

    /* ------------------------------------------------------ */
    /*              РОБОТА З ТАБЛИЦЕЮ PlanCycles              */
    /* ------------------------------------------------------ */

    /**
     * Додає нову програму (FitnessProgram) у таблицю "PlanCycles",
     * визначаючи position як (MAX(position) + 1).
     */
    public long addFitProgram(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Знаходимо наступну position
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM PlanCycles",
                null
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        fitnessProgram.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("name",           fitnessProgram.getName());
        values.put("description",    fitnessProgram.getDescription());
        values.put("creation_date",  System.currentTimeMillis());
        values.put("position",       fitnessProgram.getPosition());
        values.put("is_active",      0);

        long id = db.insert("PlanCycles", null, values);
        db.close();
        return id;
    }

    /**
     * Повертає всі програми, відсортовані за position (зчитує також name, description).
     * GymDays підтягується окремим викликом getGymDaysByPlanId(...).
     */
    public List<FitnessProgram> getAllPlans() {
        List<FitnessProgram> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description, position " +
                        "FROM PlanCycles " +
                        "ORDER BY position ASC",
                null
        );
        if (cursor.moveToFirst()) {
            do {
                long   id   = cursor.getLong(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                // int position = cursor.getInt(3); // Якщо треба присвоїти

                // Підтягуємо GymSession
                List<GymSession> sessions = getGymDaysByPlanId(id);

                FitnessProgram fp = new FitnessProgram(id, name, desc, sessions);
                // fp.setPosition(position); // Якщо зберігаєш position у FitnessProgram
                plans.add(fp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    /**
     * Отримує одну програму за ID. Якщо треба — зчитуємо position.
     */
    public FitnessProgram getPlanById(long planId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        FitnessProgram fitnessProgram = null;

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description, position " +
                        "FROM PlanCycles " +
                        "WHERE id = ?",
                new String[]{String.valueOf(planId)}
        );
        if (cursor.moveToFirst()) {
            long   id   = cursor.getLong(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            // int position = cursor.getInt(3);

            // Підтягуємо GymSessions
            List<GymSession> gymSessions = getGymDaysByPlanId(id);

            fitnessProgram = new FitnessProgram(id, name, desc, gymSessions);
            // fitnessProgram.setPosition(position);
        }
        cursor.close();
        db.close();
        return fitnessProgram;
    }

    /**
     * Оновлює назву, опис і position (перераховуючи MAX(...) + 1).
     * За бажанням можна не змінювати position при update.
     */
    public void updatePlan(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Якщо справді треба перевизначати position:
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM PlanCycles",
                null
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();
        fitnessProgram.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("name",        fitnessProgram.getName());
        values.put("description", fitnessProgram.getDescription());
        values.put("position",    fitnessProgram.getPosition());

        db.update("PlanCycles", values, "id = ?", new String[]{String.valueOf(fitnessProgram.getId())});
        db.close();
    }

    /**
     * Видаляє програму за ID.
     * Спочатку видаляємо дні (і блоки), потім саму програму —
     * або навпаки, якщо немає зовнішніх ключів.
     */
    public void deletePlan(long planId) {
        // Спочатку видаляємо дні (залежні)
        deleteGymDaysByPlanId(planId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("PlanCycles", "id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }

    /**
     * Оновлює positions у таблиці "PlanCycles" відповідно до порядку списку.
     */
    public void updatePlansPositions(List<FitnessProgram> programs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < programs.size(); i++) {
                FitnessProgram program = programs.get(i);

                ContentValues values = new ContentValues();
                values.put("position", i);

                db.update("PlanCycles",
                        values,
                        "id = ?",
                        new String[]{String.valueOf(program.getId())}
                );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /* ------------------------------------------------------ */
    /*            РОБОТА З ТАБЛИЦЕЮ GymDays (GymSession)      */
    /* ------------------------------------------------------ */

    /**
     * Додає новий тренувальний день (GymSession),
     * визначаючи position як MAX(...) + 1 серед усіх GymDays.
     *
     * За логікою можна робити "WHERE plan_id = ?" замість "SELECT ... FROM GymDays".
     */
    public long addGymDay(long planId, String dayName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Знаходимо наступну position (серед усього GymDays або конкретно plan_id)
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos " +
                        "FROM GymDays " +
                        "WHERE plan_id = ?",
                new String[]{String.valueOf(planId)}
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("plan_id",     planId);
        values.put("day_name",    dayName);
        values.put("description", description);
        values.put("position",    newPosition);

        long gymDayId = db.insert("GymDays", null, values);
        db.close();
        return gymDayId;
    }

    /**
     * Оновлює GymSession. Якщо бажано — перераховуємо position (max + 1).
     */
    public void updateGymSession(GymSession gymSession) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Якщо треба підраховувати position для цього plan_id:
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos " +
                        "FROM GymDays " +
                        "WHERE plan_id = ?",
                new String[]{String.valueOf(gymSession.getPlanId())}
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        gymSession.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("day_name",    gymSession.getName());
        values.put("description", gymSession.getDescription());
        values.put("position",    gymSession.getPosition());

        db.update("GymDays", values, "id = ?", new String[]{String.valueOf(gymSession.getId())});
        db.close();
    }

    /**
     * Видаляє день за його ID. Спочатку видаляємо блоки, потім сам день (GymDays).
     */
    public void deleteGymSession(long gymSessionId) {
        // Спочатку видаляємо TrainingBlock, які належать цьому дню
        deleteTrainingBlocksByDayId(gymSessionId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "id = ?", new String[]{String.valueOf(gymSessionId)});
        db.close();
    }

    /**
     * Видаляє всі дні плану, а також блоки, що їм належать
     * (через підзапит).
     */
    public void deleteGymDaysByPlanId(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Спочатку видаляємо блоки, які належать дням цього planId
            db.execSQL(
                    "DELETE FROM TrainingBlock " +
                            "WHERE gym_day_id IN (SELECT id FROM GymDays WHERE plan_id=?)",
                    new Object[]{planId}
            );
            // Потім видаляємо дні
            db.delete("GymDays", "plan_id = ?", new String[]{String.valueOf(planId)});
        } finally {
            db.close();
        }
    }

    /**
     * Додає список днів до плану, вираховуючи position.
     */
    public void addGymDays(long planId, List<GymSession> gymSessions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < gymSessions.size(); i++) {
            GymSession day = gymSessions.get(i);

            // Знаходимо нову позицію:
            Cursor cursor = db.rawQuery(
                    "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos " +
                            "FROM GymDays " +
                            "WHERE plan_id = ?",
                    new String[]{String.valueOf(planId)}
            );
            int newPosition = 0;
            if (cursor.moveToFirst()) {
                newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
            }
            cursor.close();

            day.setPosition(newPosition);

            ContentValues values = new ContentValues();
            values.put("plan_id",     planId);
            values.put("day_name",    day.getName().isEmpty() ? ("Day "+ (i+1)) : day.getName());
            values.put("description", day.getDescription());
            values.put("position",    day.getPosition());

            db.insert("GymDays", null, values);
            // За потреби додай і блоки
        }
        db.close();
    }

    /**
     * Отримує список GymSession певного плану, відсортований за position.
     */
    public List<GymSession> getGymDaysByPlanId(long planId) {
        List<GymSession> gymSessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, day_name, description, position " +
                        "FROM GymDays " +
                        "WHERE plan_id = ? " +
                        "ORDER BY position ASC",
                new String[]{String.valueOf(planId)}
        );
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int    id        = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String dayName   = cursor.getString(cursor.getColumnIndex("day_name"));
                @SuppressLint("Range")
                String desc      = cursor.getString(cursor.getColumnIndex("description"));
                @SuppressLint("Range")
                int    position  = cursor.getInt(cursor.getColumnIndex("position"));

                // Підтягуємо TrainingBlocks для цього дня
                List<TrainingBlock> blocks = getTrainingBlocksByDayId(id);

                GymSession session = new GymSession(id, (int) planId, blocks);
                session.setName(dayName);
                session.setDescription(desc);
                session.setPosition(position);

                gymSessions.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gymSessions;
    }

    /**
     * Оновлює позиції днів (GymSession) у таблиці "GymDays"
     * за списком, що відсортований після drag&drop.
     */
    public void updateGymDaysPositions(List<GymSession> gymSessions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < gymSessions.size(); i++) {
                GymSession session = gymSessions.get(i);

                ContentValues values = new ContentValues();
                values.put("position", i);

                db.update("GymDays",
                        values,
                        "id = ?",
                        new String[]{String.valueOf(session.getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /* ------------------------------------------------------ */
    /*           РОБОТА З ТАБЛИЦЕЮ TrainingBlock (блоки)      */
    /* ------------------------------------------------------ */

    /**
     * Додає блок (TrainingBlock), визначаючи position як max(...) + 1.
     */
    public long addTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos " +
                        "FROM TrainingBlock " +
                        "WHERE gym_day_id = ?",
                new String[]{String.valueOf(block.getGymDayId())}
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        block.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("gym_day_id",  block.getGymDayId());
        values.put("name",        block.getName());
        values.put("description", block.getDescription());
        values.put("position",    block.getPosition());

        long id = db.insert("TrainingBlock", null, values);
        db.close();
        return id;
    }

    /**
     * Оновлює блок (name, description) і (опційно) position у TrainingBlock.
     */
    public void updateTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos " +
                        "FROM TrainingBlock " +
                        "WHERE gym_day_id = ?",
                new String[]{String.valueOf(block.getGymDayId())}
        );
        int newPosition = 0;
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        block.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("name",        block.getName());
        values.put("description", block.getDescription());
        values.put("position",    block.getPosition());

        db.update("TrainingBlock",
                values,
                "id = ?",
                new String[]{String.valueOf(block.getId())}
        );
        db.close();
    }

    /**
     * Видаляє блок за ID. Перед цим знищуємо фільтри, пов'язані з блоком.
     */
    public void deleteTrainingBlock(long blockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Спочатку видаляємо фільтри
        db.delete("TrainingBlockMotion",      "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockEquipment",   "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        // Потім сам блок
        db.delete("TrainingBlock",            "id = ?", new String[]{String.valueOf(blockId)});
        db.close();
    }

    /**
     * Отримує список блоків дня, відсортованих за position.
     */
    public List<TrainingBlock> getTrainingBlocksByDayId(long gymDayId) {
        List<TrainingBlock> blocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description, position " +
                        "FROM TrainingBlock " +
                        "WHERE gym_day_id = ? " +
                        "ORDER BY position ASC",
                new String[]{String.valueOf(gymDayId)}
        );
        if (cursor.moveToFirst()) {
            do {
                long   id   = cursor.getLong(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                int    pos  = cursor.getInt(3);

                TrainingBlock block = new TrainingBlock(id, gymDayId, name, desc);
                block.setPosition(pos);

                blocks.add(block);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return blocks;
    }

    /**
     * Оновлює позиції блоків у TrainingBlock (drag&drop).
     */
    public void updateTrainingBlockPositions(List<TrainingBlock> blocks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < blocks.size(); i++) {
                TrainingBlock block = blocks.get(i);

                ContentValues values = new ContentValues();
                values.put("position", i);

                db.update("TrainingBlock",
                        values,
                        "id = ?",
                        new String[]{String.valueOf(block.getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Видаляє блоки за ID дня (gym_day_id).
     * Викликається під час видалення дня.
     */
    private void deleteTrainingBlocksByDayId(long dayId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Так само з фільтрами, якщо треба:
        db.execSQL(
                "DELETE FROM TrainingBlockMotion " +
                        "WHERE trainingBlockId IN (SELECT id FROM TrainingBlock WHERE gym_day_id=?)",
                new Object[]{dayId}
        );
        db.execSQL(
                "DELETE FROM TrainingBlockMuscleGroup " +
                        "WHERE trainingBlockId IN (SELECT id FROM TrainingBlock WHERE gym_day_id=?)",
                new Object[]{dayId}
        );
        db.execSQL(
                "DELETE FROM TrainingBlockEquipment " +
                        "WHERE trainingBlockId IN (SELECT id FROM TrainingBlock WHERE gym_day_id=?)",
                new Object[]{dayId}
        );

        db.delete("TrainingBlock", "gym_day_id = ?", new String[]{String.valueOf(dayId)});
        db.close();
    }

    /* ------------------------------------------------------ */
    /*          ФІЛЬТРИ ДЛЯ БЛОКІВ (motion, muscle, etc.)     */
    /* ------------------------------------------------------ */

    /**
     * Додає фільтр (motionType / muscleGroup / equipment) у відповідну таблицю.
     */
    public void addTrainingBlockFilter(long trainingBlockId, String filterType, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trainingBlockId", trainingBlockId);
        values.put(filterType, value);

        String tableName;
        switch (filterType) {
            case "motionType":
                tableName = "TrainingBlockMotion";
                break;
            case "muscleGroup":
                tableName = "TrainingBlockMuscleGroup";
                break;
            case "equipment":
                tableName = "TrainingBlockEquipment";
                break;
            default:
                tableName = "";
        }
        db.insert(tableName, null, values);
        db.close();
    }

    /**
     * Очищає фільтри (motion/muscle/equipment) перед збереженням нових.
     */
    public void clearTrainingBlockFilters(long trainingBlockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion",      "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockEquipment",   "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.close();
    }

    /**
     * Повертає список значень фільтра (motionType, muscleGroup, equipment)
     * із відповідної таблиці.
     */
    public List<String> getTrainingBlockFilters(long trainingBlockId, String filterType) {
        List<String> filters = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String tableName;
        switch (filterType) {
            case "motionType":
                tableName = "TrainingBlockMotion";
                break;
            case "muscleGroup":
                tableName = "TrainingBlockMuscleGroup";
                break;
            case "equipment":
                tableName = "TrainingBlockEquipment";
                break;
            default:
                tableName = "";
        }

        Cursor cursor = db.rawQuery(
                "SELECT " + filterType + " " +
                        "FROM " + tableName + " " +
                        "WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String value = cursor.getString(cursor.getColumnIndex(filterType));
                filters.add(value);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return filters;
    }

    /* ------------------------------------------------------ */
    /*  ВПРАВИ ДЛЯ БЛОКУ (ФІЛЬТРИ) : getExercisesForTrainingBlock */
    /* ------------------------------------------------------ */

    /**
     * Повертає список вправ (Exercise), що підпадають під фільтри даного блоку
     * (motion, muscleGroup, equipment).
     */
    public List<Exercise> getExercisesForTrainingBlock(long trainingBlockId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "SELECT DISTINCT e.id, e.name, e.motion, e.muscleGroups, e.equipment, e.isCustom " +
                        "FROM Exercise e " +
                        "LEFT JOIN TrainingBlockMotion tm " +
                        "    ON e.motion = tm.motionType " +
                        "LEFT JOIN TrainingBlockMuscleGroup tmg " +
                        "    ON ',' || e.muscleGroups || ',' LIKE '%,' || tmg.muscleGroup || ',%' " +
                        "LEFT JOIN TrainingBlockEquipment te " +
                        "    ON e.equipment = te.equipment " +
                        "WHERE tm.trainingBlockId = ? " +
                        "   OR tmg.trainingBlockId = ? " +
                        "   OR te.trainingBlockId = ?";

        Log.d("DB_DEBUG_SQL", "Executing SQL for Block ID: " + trainingBlockId);
        Log.d("DB_DEBUG_SQL", "SQL Query: " + query);

        Cursor cursor = db.rawQuery(
                query,
                new String[]{
                        String.valueOf(trainingBlockId),
                        String.valueOf(trainingBlockId),
                        String.valueOf(trainingBlockId)
                }
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String exerciseName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range")
                String motionStr = cursor.getString(cursor.getColumnIndex("motion"));
                @SuppressLint("Range")
                String muscleGroupsStr = cursor.getString(cursor.getColumnIndex("muscleGroups"));
                @SuppressLint("Range")
                String equipmentStr = cursor.getString(cursor.getColumnIndex("equipment"));

                Log.d("DB_DEBUG_CURSOR",
                        "Exercise: " + exerciseName +
                                " | motion=" + motionStr +
                                " | muscleGroups=" + muscleGroupsStr +
                                " | equipment=" + equipmentStr);

                // motion
                Motion motion = null;
                if (motionStr != null && !motionStr.isEmpty()) {
                    try {
                        motion = Motion.valueOf(motionStr);
                    } catch (IllegalArgumentException e) {
                        Log.e("DB_DEBUG_CURSOR", "Unknown Motion: " + motionStr, e);
                    }
                }

                // equipment
                Equipment equipment = null;
                if (equipmentStr != null && !equipmentStr.isEmpty()) {
                    try {
                        equipment = Equipment.valueOf(equipmentStr);
                    } catch (IllegalArgumentException e) {
                        Log.e("DB_DEBUG_CURSOR", "Unknown Equipment: " + equipmentStr, e);
                    }
                }

                // muscleGroups
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                if (muscleGroupsStr != null && !muscleGroupsStr.isEmpty()) {
                    String[] mgArray = muscleGroupsStr.split(",");
                    for (String mg : mgArray) {
                        mg = mg.trim();
                        try {
                            muscleGroups.add(MuscleGroup.valueOf(mg));
                        } catch (IllegalArgumentException e) {
                            Log.e("DB_DEBUG_CURSOR", "Unknown MuscleGroup: " + mg, e);
                        }
                    }
                }

                // Створюємо Exercise
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



    /**
     * Логує всю структуру:
     *   1) Усі плани (PlanCycles)
     *   2) Кожен день (GymDay) в плані
     *   3) Кожен блок (TrainingBlock) у дні
     *   4) Список фільтрів для блока (motion, muscleGroup, equipment)
     */
    public void logAllData() {
        Log.d("PlanManagerDAO", "========== START: logAllData() ==========");

        // 1) Отримуємо усі плани
        List<FitnessProgram> allPrograms = getAllPlans();
        if (allPrograms.isEmpty()) {
            Log.d("PlanManagerDAO", "Немає жодної програми (PlanCycles)");
            Log.d("PlanManagerDAO", "========== END: logAllData() ==========");
            return;
        }

        for (FitnessProgram program : allPrograms) {
            long   planId   = program.getId();
            String planName = program.getName();
            String planDesc = program.getDescription();
            // За потреби ти можеш логувати position, is_active тощо — головне, щоб
            // ти діставав їх у getAllPlans()
            Log.d("PlanManagerDAO", "PlanCycle [id=" + planId
                    + ", name=\"" + planName + "\", desc=\"" + planDesc + "\"]");

            // 2) Отримуємо всі дні для плану
            List<GymSession> sessions = getGymDaysByPlanId(program.getId()); // або getGymDaysByPlanId(planId)
            if (sessions.isEmpty()) {
                Log.d("PlanManagerDAO", "  (No GymDays in this plan)");
            } else {
                for (GymSession session : sessions) {
                    long   dayId   = session.getId();
                    String dayName = session.getName();
                    String dayDesc = session.getDescription();

                    Log.d("PlanManagerDAO", "   GymDay [id=" + dayId
                            + ", name=\"" + dayName
                            + "\", desc=\"" + dayDesc + "\"]");

                    // 3) Для кожного дня дістаємо блоки
                    List<TrainingBlock> blocks = getTrainingBlocksByDayId(session.getId());
                    if (blocks.isEmpty()) {
                        Log.d("PlanManagerDAO", "     (No TrainingBlocks in this day)");
                    } else {
                        for (TrainingBlock block : blocks) {
                            long   blockId   = block.getId();
                            String blockName = block.getName();
                            String blockDesc = block.getDescription();
                            Log.d("PlanManagerDAO", "     TrainingBlock [id=" + blockId
                                    + ", name=\"" + blockName
                                    + "\", desc=\"" + blockDesc + "\"]");

                            // 4) Логуємо фільтри (motion, muscleGroup, equipment)
                            List<String> motions = getTrainingBlockFilters(blockId, "motionType");
                            List<String> muscles = getTrainingBlockFilters(blockId, "muscleGroup");
                            List<String> equips  = getTrainingBlockFilters(blockId, "equipment");

                            if (!motions.isEmpty()) {
                                Log.d("PlanManagerDAO", "       * motions=" + motions);
                            }
                            if (!muscles.isEmpty()) {
                                Log.d("PlanManagerDAO", "       * muscles=" + muscles);
                            }
                            if (!equips.isEmpty()) {
                                Log.d("PlanManagerDAO", "       * equipment=" + equips);
                            }

                            // Можна ще для відладки дістати всі вправи цього блоку:
                            List<Exercise> blockExercises = getExercisesForTrainingBlock(blockId);
                            if (!blockExercises.isEmpty()) {
                                Log.d("PlanManagerDAO", "       * exercises in block => ");
                                for (Exercise ex : blockExercises) {
                                    Log.d("PlanManagerDAO",
                                            "         - exId=" + ex.getId()
                                                    + ", name=\"" + ex.getName() + "\"");
                                }
                            }
                        }
                    }
                }
            }
            Log.d("PlanManagerDAO", "-----------------------------------------");
        }

        Log.d("PlanManagerDAO", "========== END: logAllData() ==========");
    }

}

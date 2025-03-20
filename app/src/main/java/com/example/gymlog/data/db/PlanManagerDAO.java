package com.example.gymlog.data.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.ExerciseInBlock;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.plan.FitnessProgram;
import com.example.gymlog.data.plan.GymSession;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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



    /**Якщо count = 3, метод поверне: "?, ?, ?"
     Якщо count = 1, метод поверне: "?"
     Якщо count = 0, метод поверне порожній рядок (""), що допоможе уникнути помилки.*/
    private String getPlaceholders(int count) {
        if (count <= 0) return "";
        return new String(new char[count]).replace("\0", "?, ").replaceAll(", $", "");
    }


    /** ------------------------------------------------------ /
    /*              РОБОТА З ТАБЛИЦЕЮ PlanCycles              /
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


    //Отримати назву програми по gymDayId
    public String getProgramNameByGymDayId(long gymDayId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT p.name FROM PlanCycles p JOIN GymDays g ON p.id = g.plan_id WHERE g.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gymDayId)});

        String programName = "Невідомий план";
        if (cursor.moveToFirst()) {
            programName = cursor.getString(0);
        }
        cursor.close();
        return programName;
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

    /** ------------------------------------------------------ /
    /*            РОБОТА З ТАБЛИЦЕЮ GymDays (GymSession)      /
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


    //Отримати назву тренувального дня
    public String getGymDayNameById(long gymDayId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT day_name FROM GymDays WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(gymDayId)});

        String gymDayName = "Невідомий день";
        if (cursor.moveToFirst()) {
            gymDayName = cursor.getString(0);
        }
        cursor.close();
        return gymDayName;
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

    /** ------------------------------------------------------ /
    /*           РОБОТА З ТАБЛИЦЕЮ TrainingBlock (блоки)      /
    /* ------------------------------------------------------ /

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

        // Спочатку вибираємо з TrainingBlock (id, name, desc, position)
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

                // Створюємо проміжний об'єкт (motion, muscleGroups, equipment — поки що null / порожнє)
                // Припустимо, що маємо «повний» конструктор (long id, long gymDayId, String name, ...)
                //  ДЕ motion, muscleGroups, equipment передаємо поки null / empty, exercises = new ArrayList<>()
                TrainingBlock block = new TrainingBlock(
                        id,
                        gymDayId,
                        name,
                        desc,
                        null,                     // motion
                        new ArrayList<>(),       // muscleGroupList
                        null,                     // equipment
                        pos,
                        new ArrayList<>()        // exercises
                );

                // Тепер треба зчитати додаткові фільтри blockId=id
                loadBlockFilters(db, block);

                blocks.add(block);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return blocks;
    }

    //Отримати всі блоки
    public List<TrainingBlock> getAllTrainingBlocks() {
        List<TrainingBlock> blocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT id, gym_day_id, name, description FROM TrainingBlock";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") long gymDayId = cursor.getLong(cursor.getColumnIndex("gym_day_id"));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                    blocks.add(new TrainingBlock(id, gymDayId, name, description));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при отриманні списку тренувальних блоків", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return blocks;
    }

    public List<TrainingBlock> getTrainingBlocksForExercise(Exercise exercise) {
        List<TrainingBlock> blocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "SELECT DISTINCT tb.id, tb.gym_day_id, tb.name, tb.description " +
                        "FROM TrainingBlock tb " +
                        "LEFT JOIN TrainingBlockMotion tbm ON tb.id = tbm.trainingBlockId " +
                        "LEFT JOIN TrainingBlockMuscleGroup tbg ON tb.id = tbg.trainingBlockId " +
                        "LEFT JOIN TrainingBlockEquipment tbe ON tb.id = tbe.trainingBlockId " +
                        "WHERE tbm.motionType = ? " +
                        "   OR tbg.muscleGroup IN (" + getPlaceholders(exercise.getMuscleGroupList().size()) + ") " +
                        "   OR tbe.equipment = ?";

        List<String> argsList = new ArrayList<>();
        argsList.add(exercise.getMotion().name());

        for (MuscleGroup muscleGroup : exercise.getMuscleGroupList()) {
            argsList.add(muscleGroup.name());
        }

        argsList.add(exercise.getEquipment().name());
        String[] args = argsList.toArray(new String[0]);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, args);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") long gymDayId = cursor.getLong(cursor.getColumnIndex("gym_day_id"));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                    blocks.add(new TrainingBlock(id, gymDayId, name, description));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при отриманні блоків для вправи", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

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

    /** ------------------------------------------------------ /
    /          ФІЛЬТРИ ДЛЯ БЛОКІВ (motion, muscle, etc.)     /
    / ------------------------------------------------------ */

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

    private void loadBlockFilters(SQLiteDatabase db, TrainingBlock block) {
        long blockId = block.getId();

        // 1) Зчитуємо motions із таблиці TrainingBlockMotion
        //    Якщо припускаємо, що motion завжди один, беремо перший запис.
        //    Якщо може бути кілька — зберігаємо список або якось обробляємо.
        Cursor cMotion = db.rawQuery(
                "SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId=?",
                new String[]{ String.valueOf(blockId) }
        );
        if (cMotion.moveToFirst()) {
            // Припустимо, що беремо лише перший
            String motionStr = cMotion.getString(0);
            try {
                block.setMotion(Motion.valueOf(motionStr));
            } catch (Exception e) {
                // Якщо motionStr не підходить до enum
                Log.e("DB_DEBUG", "Unknown motion: " + motionStr, e);
            }
        }
        cMotion.close();

        // 2) Зчитуємо muscleGroup із TrainingBlockMuscleGroup
        //    Тут може бути кілька рядків, тож наповнюємо block.getMuscleGroupList()
        Cursor cMuscle = db.rawQuery(
                        "SELECT muscleGroup FROM TrainingBlockMuscleGroup " +
                        "WHERE trainingBlockId=?",
                        new String[]{ String.valueOf(blockId) }
                    );
        List<MuscleGroup> mgList = new ArrayList<>();
        if (cMuscle.moveToFirst()) {
            do {
                String mgStr = cMuscle.getString(0);
                try {
                    mgList.add(MuscleGroup.valueOf(mgStr));
                } catch (Exception e) {
                    Log.e("DB_DEBUG", "Unknown muscleGroup: " + mgStr, e);
                }
            } while (cMuscle.moveToNext());
        }
        cMuscle.close();
        block.setMuscleGroupList(mgList);

        // 3) Зчитуємо equipment з TrainingBlockEquipment
        //    Якщо equipment лише один, беремо перший.
        Cursor cEquip = db.rawQuery("SELECT equipment " +
                        "FROM TrainingBlockEquipment WHERE trainingBlockId=?",
                        new String[]{ String.valueOf(blockId) }
                    );
        if (cEquip.moveToFirst()) {
            String eqStr = cEquip.getString(0);
            try {
                block.setEquipment(Equipment.valueOf(eqStr));
            } catch (Exception e) {
                Log.e("DB_DEBUG", "Unknown equipment: " + eqStr, e);
            }
        }
        cEquip.close();
    }


    /** --------------------------------------------------------- /
    /                       ВПРАВИ ДЛЯ БЛОКУ                     /
    / --------------------------------------------------------- */

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

    public void updateTrainingBlockExercises(long blockId, List<ExerciseInBlock> exercises) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete("TrainingBlockExercises", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});

            ContentValues values = new ContentValues();
            for (ExerciseInBlock exercise : exercises) {
                values.clear();
                values.put("trainingBlockId", blockId);
                values.put("exerciseId", exercise.getId());
                values.put("position", exercise.getPosition());
                db.insert("TrainingBlockExercises", null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB_CRASH", "Помилка при оновленні вправ у блоці", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }







    /**
     * Повертає список обраних вправ для конкретного блоку (білий список).
     */
    public List<ExerciseInBlock> getBlockExercises(long trainingBlockId) {
        List<ExerciseInBlock> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT e.id, e.name, e.motion, e.muscleGroups, e.equipment, e.isCustom, tbe.position " +
                "FROM TrainingBlockExercises tbe " +
                "JOIN Exercise e ON e.id = tbe.exerciseId " +
                "WHERE tbe.trainingBlockId = ? " +
                "ORDER BY tbe.position ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(trainingBlockId)});
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String motionStr = cursor.getString(2);
            String muscleGroupsStr = cursor.getString(3);
            String equipmentStr = cursor.getString(4);
            boolean isCustom = cursor.getInt(5) == 1;
            int position = cursor.getInt(6);

            // Парсимо ENUM-и
            Motion motion = parseMotion(motionStr);
            Equipment equipment = parseEquipment(equipmentStr);
            List<MuscleGroup> muscleGroups = parseMuscleGroups(muscleGroupsStr);

            ExerciseInBlock exercise = new ExerciseInBlock(id, name, motion, muscleGroups, equipment, position);
            exercise.setCustom(isCustom);

            exercises.add(exercise);
        }
        cursor.close();
        db.close();

        return exercises;
    }


    // Допоміжні методи для парсингу ENUM (для чистоти коду)
    private Motion parseMotion(String motionStr) {
        if (motionStr != null && !motionStr.isEmpty()) {
            try { return Motion.valueOf(motionStr); } catch (Exception ignored) { }
        }
        return null;
    }

    private Equipment parseEquipment(String equipmentStr) {
        if (equipmentStr != null && !equipmentStr.isEmpty()) {
            try { return Equipment.valueOf(equipmentStr); } catch (Exception ignored) { }
        }
        return null;
    }

    private List<MuscleGroup> parseMuscleGroups(String muscleGroupsStr) {
        List<MuscleGroup> groups = new ArrayList<>();
        if (muscleGroupsStr != null && !muscleGroupsStr.isEmpty()) {
            for (String mg : muscleGroupsStr.split(",")) {
                try { groups.add(MuscleGroup.valueOf(mg.trim())); } catch (Exception ignored) { }
            }
        }
        return groups;
    }



    /**
     * Додає вправу до тренувального блоку.
     */
    public void addExerciseToBlock(long blockId, long exerciseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("trainingBlockId", blockId);
        cv.put("exerciseId", exerciseId);

        // Щоб уникнути дублікатів
        db.insertWithOnConflict("TrainingBlockExercises", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }



    /**
     * Видаляє вправу з тренувального блоку.
     */
    public void removeExerciseFromBlock(long blockId, long exerciseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("TrainingBlockExercises",
                "trainingBlockId=? AND exerciseId=?",
                new String[]{String.valueOf(blockId), String.valueOf(exerciseId)});

        db.close();
    }






    /**
     * Логує всю структуру:
     *   1) Усі плани (PlanCycles)
     *   2) Кожен день (GymDay) в плані
     *   3) Кожен блок (TrainingBlock) у дні
     *   4) Список фільтрів для блока (motion, muscleGroup, equipment)
     */
    public void logAllData() {
        Log.d("logAllData", "========== START: logAllData() ==========");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1️⃣ Логуємо всі плани
        Cursor cursorPlans = db.rawQuery("SELECT id, name, description FROM PlanCycles", null);
        while (cursorPlans.moveToNext()) {
            long planId = cursorPlans.getLong(0);
            String planName = cursorPlans.getString(1);
            String planDesc = cursorPlans.getString(2);
            Log.d("logAllData", "PlanCycle [id=" + planId + ", name=" + planName + ", desc=" + planDesc + "]");

            // 2️⃣ Логуємо всі дні для кожного плану
            Cursor cursorDays = db.rawQuery("SELECT id, day_name, description FROM GymDays WHERE plan_id = ?", new String[]{String.valueOf(planId)});
            while (cursorDays.moveToNext()) {
                long dayId = cursorDays.getLong(0);
                String dayName = cursorDays.getString(1);
                String dayDesc = cursorDays.getString(2);
                Log.d("logAllData", "  GymDay [id=" + dayId + ", name=" + dayName + ", desc=" + dayDesc + "]");

                // 3️⃣ Логуємо всі блоки для кожного дня
                Cursor cursorBlocks = db.rawQuery("SELECT id, name, description FROM TrainingBlock WHERE gym_day_id = ?", new String[]{String.valueOf(dayId)});
                while (cursorBlocks.moveToNext()) {
                    long blockId = cursorBlocks.getLong(0);
                    String blockName = cursorBlocks.getString(1);
                    String blockDesc = cursorBlocks.getString(2);
                    Log.d("logAllData", "    TrainingBlock [id=" + blockId + ", name=" + blockName + ", desc=" + blockDesc + "]");

                    // 4️⃣ Логуємо всі вправи у кожному блоці
                    Cursor cursorExercises = db.rawQuery(
                            "SELECT e.id, e.name FROM TrainingBlockExercises tbe JOIN Exercise e ON e.id = tbe.exerciseId WHERE tbe.trainingBlockId = ?",
                            new String[]{String.valueOf(blockId)}
                    );
                    while (cursorExercises.moveToNext()) {
                        long exId = cursorExercises.getLong(0);
                        String exName = cursorExercises.getString(1);
                        Log.d("logAllData", "      Exercise [id=" + exId + ", name=" + exName + "]");
                    }
                    cursorExercises.close();
                }
                cursorBlocks.close();
            }
            cursorDays.close();
        }
        cursorPlans.close();
        db.close();

        Log.d("logAllData", "========== END: logAllData() ==========");
    }


}

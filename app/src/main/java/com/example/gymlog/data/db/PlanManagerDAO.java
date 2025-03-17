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
 * DAO-клас для управління планами тренувань (FitnessProgram), днями (GymSession),
 * та блоками (TrainingBlock). Здійснює:
 * - створення/оновлення/видалення планів, днів і блоків
 * - отримання списків із бази
 * - роботу з фільтрами для блоку
 */
public class PlanManagerDAO {

    private final DBHelper dbHelper;
    private final Context context;

    /**
     * Конструктор DAO
     */
    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    /** ------------------------------------------------------ *
    *              РОБОТА З ТАБЛИЦЕЮ PlanCycles               *
    * ------------------------------------------------------ */

    /**
     * Додає нову програму тренувань (FitnessProgram) у таблицю "PlanCycles".
     */
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

    /**
     * Отримує список усіх планів (FitnessProgram) із таблиці "PlanCycles".
     */
    public List<FitnessProgram> getAllPlans() {
        List<FitnessProgram> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles", null);

        if (cursor.moveToFirst()) {
            do {
                long id          = cursor.getLong(0);
                String name      = cursor.getString(1);
                String desc      = cursor.getString(2);
                List<GymSession> gymSessions = getGymDaysByPlanId(id);

                plans.add(new FitnessProgram(id, name, desc, gymSessions));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }

    /**
     * Отримує одну програму (FitnessProgram) за її ID.
     */
    public FitnessProgram getPlanById(long planId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        FitnessProgram fitnessProgram = null;

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description FROM PlanCycles WHERE id = ?",
                new String[]{String.valueOf(planId)}
        );
        if (cursor.moveToFirst()) {
            String name        = cursor.getString(1);
            String description = cursor.getString(2);
            List<GymSession> gymSessions = getGymDaysByPlanId(planId);

            fitnessProgram = new FitnessProgram(planId, name, description, gymSessions);
        }
        cursor.close();
        db.close();
        return fitnessProgram;
    }

    /**
     * Оновлює назву та опис існуючої програми тренувань.
     */
    public void updatePlan(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", fitnessProgram.getName());
        values.put("description", fitnessProgram.getDescription());

        db.update("PlanCycles", values, "id = ?", new String[]{String.valueOf(fitnessProgram.getId())});
        db.close();
    }

    /**
     * Видаляє програму тренувань за ID.
     */
    public void deletePlan(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("PlanCycles", "id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }

    /** ------------------------------------------------------ *
    *          РОБОТА З ТАБЛИЦЕЮ GymDays (GymSession)        *
    * ------------------------------------------------------ */

    /**
     * Додає новий тренувальний день (GymSession) до певного плану.
     */
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

    /**
     * Оновлює тренувальний день (GymSession) у таблиці "GymDays".
     */
    public void updateGymSession(GymSession gymSession) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day_name", gymSession.getName());
        values.put("description", gymSession.getDescription());

        db.update("GymDays", values, "id = ?", new String[]{String.valueOf(gymSession.getId())});
        db.close();
    }

    /**
     * Видаляє тренувальний день із таблиці "GymDays".
     */
    public void deleteGymSession(long gymSessionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "id = ?", new String[]{String.valueOf(gymSessionId)});
        db.close();
    }

    /**
     * Видаляє всі дні плану (для повного оновлення списку днів).
     */
    public void deleteGymDaysByPlanId(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "plan_id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }

    /**
     * Додає список днів (GymSession) у план (planId).
     */
    public void addGymDays(long planId, List<GymSession> gymSessions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < gymSessions.size(); i++) {
            GymSession day = gymSessions.get(i);

            ContentValues values = new ContentValues();
            values.put("plan_id", planId);
            // day_name можна призначити, наприклад, i+1, або бравши з day.getName()
            values.put("day_name", i + 1);
            values.put("description", day.getDescription());

            // Вставляємо в БД
            db.insert("GymDays", null, values);
            // Можна тут додавати TrainingBlocks, якщо потрібно
        }

        db.close();
    }

    /**
     * Отримує всі дні (GymSession) конкретного плану за ID плану.
     */
    public List<GymSession> getGymDaysByPlanId(long planId) {
        List<GymSession> gymSessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, day_name, description FROM GymDays WHERE plan_id = ?",
                new String[]{String.valueOf(planId)}
        );
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id         = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String dayName = cursor.getString(cursor.getColumnIndex("day_name"));
                @SuppressLint("Range") String desc    = cursor.getString(cursor.getColumnIndex("description"));

                // Для кожного дня отримуємо TrainingBlocks
                List<TrainingBlock> trainingBlocks = getTrainingBlocksByDayId(id);

                // Формуємо об’єкт GymSession
                GymSession gymSession = new GymSession(id, (int) planId, trainingBlocks);
                gymSession.setName(dayName);
                gymSession.setDescription(desc);

                gymSessions.add(gymSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return gymSessions;
    }

    /** ------------------------------------------------------ *
    *        РОБОТА З ТАБЛИЦЕЮ TrainingBlock (блоки)         *
    * ------------------------------------------------------ */

    /**
     * Додає новий тренувальний блок (TrainingBlock) у таблицю "TrainingBlock".
     * Визначає позицію (максимальне position + 1).
     */
    public long addTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Знаходимо наступну position
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

        // Присвоюємо блокові цю позицію
        block.setPosition(newPosition);

        // Записуємо в БД
        ContentValues values = new ContentValues();
        values.put("gym_day_id", block.getGymDayId());
        values.put("name", block.getName());
        values.put("description", block.getDescription());
        values.put("position", block.getPosition());

        long id = db.insert("TrainingBlock", null, values);
        db.close();
        return id;
    }

    /**
     * Оновлює тренувальний блок (name, description) у таблиці "TrainingBlock".
     */
    public void updateTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", block.getName());
        values.put("description", block.getDescription());

        db.update("TrainingBlock", values, "id = ?", new String[]{String.valueOf(block.getId())});
        db.close();
    }

    /**
     * Видаляє тренувальний блок (TrainingBlock), а також пов'язані фільтри
     * з таблиць TrainingBlockMotion, TrainingBlockMuscleGroup, TrainingBlockEquipment.
     */
    public void deleteTrainingBlock(long blockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockEquipment", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlock", "id = ?", new String[]{String.valueOf(blockId)});
        db.close();
    }

    /**
     * Повертає список блоків (TrainingBlock) для певного дня (gymDayId),
     * відсортованих за полем "position".
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
                long id          = cursor.getLong(0);
                String name      = cursor.getString(1);
                String desc      = cursor.getString(2);
                int position     = cursor.getInt(3);

                // Створюємо block, ставимо позицію
                TrainingBlock block = new TrainingBlock(id, gymDayId, name, desc);
                block.setPosition(position);
                blocks.add(block);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return blocks;
    }

    /**
     * Оновлює позиції блоків (TrainingBlock) у таблиці "TrainingBlock"
     * на основі поточного порядку в списку (наприклад, після drag&drop).
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
                        new String[]{String.valueOf(block.getId())}
                );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /** ------------------------------------------------------ *
    *      ФІЛЬТРИ ДЛЯ БЛОКІВ (motion, muscle, equipment)    *
    * ------------------------------------------------------ */

    /**
     * Додає фільтр до блоку (motionType / muscleGroup / equipment),
     * записуючи в відповідну таблицю (TrainingBlockMotion / TrainingBlockMuscleGroup / TrainingBlockEquipment).
     */
    public void addTrainingBlockFilter(long trainingBlockId, String filterType, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trainingBlockId", trainingBlockId);
        values.put(filterType, value);

        String tableName = "";
        if (filterType.equals("motionType")) {
            tableName = "TrainingBlockMotion";
        } else if (filterType.equals("muscleGroup")) {
            tableName = "TrainingBlockMuscleGroup";
        } else if (filterType.equals("equipment")) {
            tableName = "TrainingBlockEquipment";
        }

        db.insert(tableName, null, values);
        db.close();
    }

    /**
     * Видаляє усі фільтри для блоку, щоб потім можна було зберегти нові (clear + add).
     */
    public void clearTrainingBlockFilters(long trainingBlockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockEquipment", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.close();
    }

    /**
     * Отримуємо список фільтрів за певним типом (motionType, muscleGroup, equipment)
     * з таблиць TrainingBlockMotion / TrainingBlockMuscleGroup / TrainingBlockEquipment.
     */
    public List<String> getTrainingBlockFilters(long trainingBlockId, String filterType) {
        List<String> filters = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String tableName = "";
        if (filterType.equals("motionType")) {
            tableName = "TrainingBlockMotion";
        } else if (filterType.equals("muscleGroup")) {
            tableName = "TrainingBlockMuscleGroup";
        } else if (filterType.equals("equipment")) {
            tableName = "TrainingBlockEquipment";
        }

        Cursor cursor = db.rawQuery(
                "SELECT " + filterType +
                        " FROM " + tableName +
                        " WHERE trainingBlockId = ?",
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

    /** ------------------------------------------------------ *
    *           МЕТОД ДЛЯ ОТРИМАННЯ ВПРАВ БЛОКУ (ФІЛЬТР)      *
    * ------------------------------------------------------ */

    /**
     * Повертає список вправ (Exercise), що підпадають під фільтри блоку (motion, muscle, equipment).
     */
    public List<Exercise> getExercisesForTrainingBlock(long trainingBlockId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запит з LEFT JOIN-ами для зв’язування Exercise з TrainingBlock* таблицями
        String query =
                "SELECT DISTINCT e.id, e.name, e.motion, e.muscleGroups, e.equipment, e.isCustom " +
                        "FROM Exercise e " +
                        "LEFT JOIN TrainingBlockMotion tm ON e.motion = tm.motionType " +
                        "LEFT JOIN TrainingBlockMuscleGroup tmg ON ',' || e.muscleGroups || ',' LIKE '%,' || tmg.muscleGroup || ',%' " +
                        "LEFT JOIN TrainingBlockEquipment te ON e.equipment = te.equipment " +
                        "WHERE tm.trainingBlockId = ? OR tmg.trainingBlockId = ? OR te.trainingBlockId = ?";

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
                // Зчитуємо поля
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
                        Log.e("DB_DEBUG_CURSOR",
                                "Unknown Motion enum name: " + motionStr,
                                e
                        );
                    }
                }

                // equipment
                Equipment equipment = null;
                if (equipmentStr != null && !equipmentStr.isEmpty()) {
                    try {
                        equipment = Equipment.valueOf(equipmentStr);
                    } catch (IllegalArgumentException e) {
                        Log.e("DB_DEBUG_CURSOR",
                                "Unknown Equipment enum name: " + equipmentStr,
                                e
                        );
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
                            Log.e("DB_DEBUG_CURSOR",
                                    "Unknown MuscleGroup enum name: " + mg,
                                    e
                            );
                        }
                    }
                }

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
}

package com.example.gymlog.data.local.legacy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.example.gymlog.domain.model.exercise.Equipment;
import com.example.gymlog.domain.model.exercise.Exercise;
import com.example.gymlog.domain.model.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.exercise.Motion;
import com.example.gymlog.domain.model.exercise.MuscleGroup;
import com.example.gymlog.domain.model.plan.FitnessProgram;
import com.example.gymlog.domain.model.plan.GymSession;
import com.example.gymlog.domain.model.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO-клас для управління програмами (PlanCycles)
 */
public class PlanManagerDAO {

    private final DBHelper dbHelper;

    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }


    // -------------------------------------------------------
    //                   Програми (PlanCycles)
    // -------------------------------------------------------

    /**
     * Додає нову програму в таблицю "PlanCycles"
     *
     * @param fitnessProgram об’єкт програми
     * @return ID нової програми
     */
    public long addFitProgram(FitnessProgram fitnessProgram) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Знаходимо наступну позицію
        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM PlanCycles", null
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        fitnessProgram.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("name", fitnessProgram.getName());
        values.put("description", fitnessProgram.getDescription());
        values.put("creation_date", System.currentTimeMillis());
        values.put("position", newPosition);
        values.put("is_active", 0);

        long id = db.insert("PlanCycles", null, values);
        db.close();

        if (id == -1) {
            throw new SQLException("Помилка під час створення програми (addFitProgram)");
        }

        return id;
    }



    /**
     * Повертає всі програми з їхніми днями
     */
    public List<FitnessProgram> getAllPlans() {
        List<FitnessProgram> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description, position FROM PlanCycles ORDER BY position ASC",
                null
        );
        if (cursor.moveToFirst()) {
            do {
                long id         = cursor.getLong(0);
                String name     = cursor.getString(1);
                String desc     = cursor.getString(2);
                int position    = cursor.getInt(3);

                List<GymSession> sessions = getGymDaysByPlanId(id);
                FitnessProgram plan = new FitnessProgram(id, name, desc, sessions);
                plan.setPosition(position);
                plans.add(plan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return plans;
    }


    /**
     * Оновлює назву та опис програми
     */
    public void updatePlan(FitnessProgram program) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", program.getName());
        values.put("description", program.getDescription());

        db.update(
                "PlanCycles",
                values,
                "id = ?",
                new String[]{String.valueOf(program.getId())}
        );

        db.close();
    }

    /**
     * Видаляє програму за ID разом з її днями
     */
    public void deletePlan(long planId) {
        deleteGymDaysByPlanId(planId); // Очистка залежностей (GymDays + TrainingBlocks)
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
                FitnessProgram p = programs.get(i);

                ContentValues values = new ContentValues();
                values.put("position", i);

                db.update(
                        "PlanCycles",
                        values,
                        "id = ?",
                        new String[]{String.valueOf(p.getId())}
                );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // ------------------------------------------------------
//         РОБОТА З ТАБЛИЦЕЮ GymDays (GymSession)
// ------------------------------------------------------

    /**
     * Додає новий день тренування до плану з авто-обрахунком position.
     */
    public long addGymDay(long planId, String dayName, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM GymDays WHERE plan_id = ?",
                new String[]{String.valueOf(planId)}
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("plan_id",     planId);
        values.put("day_name",    dayName);
        values.put("description", description);
        values.put("position",    newPosition);

        long id = db.insert("GymDays", null, values);
        db.close();

        if (id == -1) {
            throw new SQLException("Помилка під час додавання тренувального дня (addGymDay)");
        }

        return id;
    }


    /**
     * Оновлює дані GymSession (назву, опис, позицію).
     * Позиція розраховується автоматично.
     */
    public void updateGymSession(GymSession gymSession) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

//        int newPosition = 0;
//        Cursor cursor = db.rawQuery(
//                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM GymDays WHERE plan_id = ?",
//                new String[]{String.valueOf(gymSession.getPlanId())}
//        );
//        if (cursor.moveToFirst()) {
//            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
//        }
//        cursor.close();
//
//        gymSession.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("day_name",    gymSession.getName());
        values.put("description", gymSession.getDescription());
        values.put("position",    gymSession.getPosition());

        db.update("GymDays", values, "id = ?", new String[]{String.valueOf(gymSession.getId())});
        db.close();
    }

    /**
     * Видаляє конкретний день тренування за ID разом із усіма його блоками.
     */
    public void deleteGymSession(long gymSessionId) {
        deleteTrainingBlocksByDayId(gymSessionId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("GymDays", "id = ?", new String[]{String.valueOf(gymSessionId)});
        db.close();
    }

    /**
     * Видаляє всі дні тренування, що належать плану, та їхні блоки.
     */
    public void deleteGymDaysByPlanId(long planId) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            // Спочатку видаляємо блоки, пов’язані з днями
            db.execSQL(
                    "DELETE FROM TrainingBlock WHERE gym_day_id IN (SELECT id FROM GymDays WHERE plan_id=?)",
                    new Object[]{planId}
            );

            // Потім самі дні
            db.delete("GymDays", "plan_id = ?", new String[]{String.valueOf(planId)});
        }
    }


    /**
     * Повертає всі дні тренування певного плану (GymSession), включаючи блоки.
     */
    public List<GymSession> getGymDaysByPlanId(long planId) {
        List<GymSession> sessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, day_name, description, position FROM GymDays WHERE plan_id = ? ORDER BY position ASC",
                new String[]{String.valueOf(planId)}
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id          = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name     = cursor.getString(cursor.getColumnIndex("day_name"));
                @SuppressLint("Range") String desc     = cursor.getString(cursor.getColumnIndex("description"));
                @SuppressLint("Range") int position    = cursor.getInt(cursor.getColumnIndex("position"));

                List<TrainingBlock> blocks = getTrainingBlocksByDayId(id);

                GymSession session = new GymSession(id, (int) planId, blocks);
                session.setName(name);
                session.setDescription(desc);
                session.setPosition(position);
                sessions.add(session);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return sessions;
    }

    /**
     * Оновлює порядок (position) днів у GymDays після drag & drop.
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


// ------------------------------------------------------
//         РОБОТА З ТАБЛИЦЕЮ TrainingBlock (блоки)
// ------------------------------------------------------

    /**
     * Додає новий блок до GymDay з позицією (MAX + 1)
     */
    public long addTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM TrainingBlock WHERE gym_day_id = ?",
                new String[]{String.valueOf(block.getGymDayId())}
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        block.setPosition(newPosition);

        ContentValues values = new ContentValues();
        values.put("gym_day_id",  block.getGymDayId());
        values.put("name",        block.getName());
        values.put("description", block.getDescription());
        values.put("position",    newPosition);

        long id = db.insert("TrainingBlock", null, values);
        db.close();

        if (id == -1) {
            throw new SQLException("Помилка під час додавання TrainingBlock");
        }

        return id;
    }

    /**
     * Оновлює дані блоку (назва, опис, позиція).
     */
    public void updateTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("name",        block.getName());
        values.put("description", block.getDescription());
        values.put("position",    block.getPosition());

        db.update("TrainingBlock", values, "id = ?", new String[]{String.valueOf(block.getId())});
        db.close();
    }

    /**
     * Видаляє блок і пов'язані з ним фільтри.
     */
    public void deleteTrainingBlock(long blockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("TrainingBlockMotion",      "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlockEquipment",   "trainingBlockId = ?", new String[]{String.valueOf(blockId)});
        db.delete("TrainingBlock",            "id = ?",              new String[]{String.valueOf(blockId)});

        db.close();
    }

    /**
     * Отримує список блоків певного дня, включаючи вправи та фільтри.
     */
    public List<TrainingBlock> getTrainingBlocksByDayId(long gymDayId) {
        List<TrainingBlock> blocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, name, description, position FROM TrainingBlock WHERE gym_day_id = ? ORDER BY position ASC",
                new String[]{String.valueOf(gymDayId)}
        );

        if (cursor.moveToFirst()) {
            do {
                long id        = cursor.getLong(0);
                String name    = cursor.getString(1);
                String desc    = cursor.getString(2);
                int position   = cursor.getInt(3);

                TrainingBlock block = new TrainingBlock(
                        id, gymDayId, name, desc,
                        null, new ArrayList<>(), null,
                        position, new ArrayList<>()
                );

                loadBlockFilters(block);
                block.setExercises(getBlockExercises(id));

                blocks.add(block);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return blocks;
    }


    /**
     * Оновлює позиції блоків у межах GymDay (drag & drop).
     */
    public void updateTrainingBlockPositions(List<TrainingBlock> blocks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < blocks.size(); i++) {
                TrainingBlock block = blocks.get(i);

                ContentValues values = new ContentValues();
                values.put("position", i);

                db.update("TrainingBlock", values, "id = ?", new String[]{String.valueOf(block.getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Видаляє всі блоки, що належать певному GymDay, разом з фільтрами.
     */
    private void deleteTrainingBlocksByDayId(long dayId) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {

            db.execSQL(
                    "DELETE FROM TrainingBlockMotion WHERE trainingBlockId IN " +
                            "(SELECT id FROM TrainingBlock WHERE gym_day_id = ?)", new Object[]{dayId}
            );
            db.execSQL(
                    "DELETE FROM TrainingBlockMuscleGroup WHERE trainingBlockId IN " +
                            "(SELECT id FROM TrainingBlock WHERE gym_day_id = ?)", new Object[]{dayId}
            );
            db.execSQL(
                    "DELETE FROM TrainingBlockEquipment WHERE trainingBlockId IN " +
                            "(SELECT id FROM TrainingBlock WHERE gym_day_id = ?)", new Object[]{dayId}
            );
            db.delete("TrainingBlock", "gym_day_id = ?", new String[]{String.valueOf(dayId)});

        }
    }

// ------------------------------------------------------
//     ФІЛЬТРИ ДЛЯ БЛОКІВ (motion, muscleGroup, equipment)
// ------------------------------------------------------

    /**
     * Додає значення фільтра (motionType / muscleGroup / equipment)
     * у відповідну таблицю для заданого блока.
     */
    public void addTrainingBlockFilter(long trainingBlockId, String filterType, String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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
                db.close();
                throw new IllegalArgumentException("Невідомий тип фільтра: " + filterType);
        }

        ContentValues values = new ContentValues();
        values.put("trainingBlockId", trainingBlockId);
        values.put(filterType, value);

        db.insert(tableName, null, values);
        db.close();
    }

    /**
     * Видаляє всі фільтри для заданого блока (очищення перед збереженням нових).
     */
    public void clearTrainingBlockFilters(long trainingBlockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockMotion",      "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockMuscleGroup", "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.delete("TrainingBlockEquipment",   "trainingBlockId = ?", new String[]{String.valueOf(trainingBlockId)});
        db.close();
    }

    /**
     * Отримує список значень заданого типу фільтра для блока.
     * Наприклад: getTrainingBlockFilters(id, "muscleGroup")
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
                db.close();
                throw new IllegalArgumentException("Невідомий тип фільтра: " + filterType);
        }

        Cursor cursor = db.rawQuery(
                "SELECT " + filterType + " FROM " + tableName + " WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );

        if (cursor.moveToFirst()) {
            do {
                filters.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return filters;
    }
    /**
     * Завантажує всі фільтри (motion, muscleGroups, equipment)
     * для блоку і присвоює їх у відповідні поля об'єкта `block`.
     */
    private void loadBlockFilters(TrainingBlock block) {
        long blockId = block.getId();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1. Motion (може бути кілька)
        Cursor cMotion = db.rawQuery(
                "SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId = ?",
                new String[]{String.valueOf(blockId)}
        );
        List<Motion> motions = new ArrayList<>();
        if (cMotion.moveToFirst()) {
            do {
                try {
                    motions.add(Motion.valueOf(cMotion.getString(0)));
                } catch (Exception e) {
                    Log.e("DB_DEBUG", "Unknown motion: " + cMotion.getString(0), e);
                }
            } while (cMotion.moveToNext());
        }
        cMotion.close();
        block.setMotions(motions);

        // 2. MuscleGroups (може бути кілька)
        Cursor cMuscle = db.rawQuery(
                "SELECT muscleGroup FROM TrainingBlockMuscleGroup WHERE trainingBlockId = ?",
                new String[]{String.valueOf(blockId)}
        );
        List<MuscleGroup> muscleGroups = new ArrayList<>();
        if (cMuscle.moveToFirst()) {
            do {
                try {
                    muscleGroups.add(MuscleGroup.valueOf(cMuscle.getString(0)));
                } catch (Exception e) {
                    Log.e("DB_DEBUG", "Unknown muscleGroup: " + cMuscle.getString(0), e);
                }
            } while (cMuscle.moveToNext());
        }
        cMuscle.close();
        block.setMuscleGroupList(muscleGroups);

        // 3. Equipment (може бути кілька)
        Cursor cEquip = db.rawQuery(
                "SELECT equipment FROM TrainingBlockEquipment WHERE trainingBlockId = ?",
                new String[]{String.valueOf(blockId)}
        );
        List<Equipment> equipmentList = new ArrayList<>();
        if (cEquip.moveToFirst()) {
            do {
                try {
                    equipmentList.add(Equipment.valueOf(cEquip.getString(0)));
                } catch (Exception e) {
                    Log.e("DB_DEBUG", "Unknown equipment: " + cEquip.getString(0), e);
                }
            } while (cEquip.moveToNext());
        }
        cEquip.close();
        block.setEquipmentList(equipmentList);

        db.close();
    }


    // ---------------------------------------------------------
//         ВПРАВИ У БЛОЦІ (фільтровані і вибрані)
// ---------------------------------------------------------

    /**
     * Повертає список вправ, що відповідають фільтрам блоку.
     * Фільтри вибираються з таблиць:
     * - TrainingBlockMotion (motionType)
     * - TrainingBlockEquipment (equipment)
     * - TrainingBlockMuscleGroup (muscleGroup)
     *
     * Якщо хоча б один з цих фільтрів присутній, повертаються вправи,
     * які відповідають усім заданим умовам.
     * Якщо фільтрів немає – повертається порожній список.
     *
     * @param trainingBlockId ID блоку тренування
     * @return список вправ, що відповідають фільтрам блоку
     */
    public List<Exercise> recommendExercisesForTrainingBlock(long trainingBlockId) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Отримання motionType фільтрів
        List<String> motionFilters = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );
        while (cursor.moveToNext()) {
            motionFilters.add(cursor.getString(0));
        }
        cursor.close();

        // Отримання equipment фільтрів
        List<String> equipmentFilters = new ArrayList<>();
        cursor = db.rawQuery(
                "SELECT equipment FROM TrainingBlockEquipment WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );
        while (cursor.moveToNext()) {
            equipmentFilters.add(cursor.getString(0));
        }
        cursor.close();

        // Отримання muscleGroup фільтрів
        List<String> muscleFilters = new ArrayList<>();
        cursor = db.rawQuery(
                "SELECT muscleGroup FROM TrainingBlockMuscleGroup WHERE trainingBlockId = ?",
                new String[]{String.valueOf(trainingBlockId)}
        );
        while (cursor.moveToNext()) {
            muscleFilters.add(cursor.getString(0));
        }
        cursor.close();

        // Якщо жоден з фільтрів не задано – повертаємо порожній список
        if (motionFilters.isEmpty() && equipmentFilters.isEmpty() && muscleFilters.isEmpty()) {
            db.close();
            return exercises;
        }

        // Побудова запиту динамічно, залежно від наявних фільтрів
        StringBuilder queryBuilder = new StringBuilder();
        List<String> args = new ArrayList<>();

        // Базовий запит – вибірка з таблиці Exercise
        queryBuilder.append("SELECT DISTINCT e.id, e.name, e.description, e.motion, e.muscleGroups, e.equipment, e.isCustom FROM Exercise e WHERE 1=1 ");

        // Якщо задані motion фільтри – додаємо умову
        if (!motionFilters.isEmpty()) {
            queryBuilder.append(" AND e.motion IN (");
            for (int i = 0; i < motionFilters.size(); i++) {
                queryBuilder.append("?");
                if (i < motionFilters.size() - 1) {
                    queryBuilder.append(", ");
                }
                args.add(motionFilters.get(i));
            }
            queryBuilder.append(") ");
        }

        // Якщо задані equipment фільтри – додаємо умову
        if (!equipmentFilters.isEmpty()) {
            queryBuilder.append(" AND e.equipment IN (");
            for (int i = 0; i < equipmentFilters.size(); i++) {
                queryBuilder.append("?");
                if (i < equipmentFilters.size() - 1) {
                    queryBuilder.append(", ");
                }
                args.add(equipmentFilters.get(i));
            }
            queryBuilder.append(") ");
        }

        // Якщо задані muscleGroup фільтри – додаємо умову
        // Поле muscleGroups містить текстовий список (наприклад, через коми), тому використовуємо оператор LIKE
        if (!muscleFilters.isEmpty()) {
            queryBuilder.append(" AND (");
            for (int i = 0; i < muscleFilters.size(); i++) {
                queryBuilder.append("e.muscleGroups LIKE ?");
                args.add("%" + muscleFilters.get(i) + "%");
                if (i < muscleFilters.size() - 1) {
                    queryBuilder.append(" OR ");
                }
            }
            queryBuilder.append(") ");
        }

        String query = queryBuilder.toString();
        Cursor result = db.rawQuery(query, args.toArray(new String[0]));

        // Обробка результатів запиту – створення об'єктів Exercise
        if (result.moveToFirst()) {
            do {
                long id = result.getLong(0);
                String name = result.getString(1);
                String description = result.getString(2);
                Motion motion = parseMotion(result.getString(3));             // Метод перетворення рядка у Motion
                List<MuscleGroup> muscleGroups = parseMuscleGroups(result.getString(4)); // Метод перетворення рядка у список MuscleGroup
                Equipment equipment = parseEquipment(result.getString(5));      // Метод перетворення рядка у Equipment

                exercises.add(new Exercise(id, name, description, motion, muscleGroups, equipment));
            } while (result.moveToNext());
        }

        result.close();
        db.close();
        return exercises;
    }



    /**
     * Повертає список вправ, що додані до блоку (білий список).
     */
    public List<ExerciseInBlock> getBlockExercises(long trainingBlockId) {
        List<ExerciseInBlock> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query =
                "SELECT tbe.id AS linkId, e.id AS exerciseId, " +
                        "e.name, e.description, e.motion, e.muscleGroups, e.equipment, e.isCustom, tbe.position " +
                        "FROM TrainingBlockExercises tbe " +
                        "JOIN Exercise e ON e.id = tbe.exerciseId " +
                        "WHERE tbe.trainingBlockId = ? " +
                        "ORDER BY tbe.position ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(trainingBlockId)});

        while (cursor.moveToNext()) {
            long linkId = cursor.getLong(0);
            long exerciseId = cursor.getLong(1);
            String name = cursor.getString(2);
            String description = cursor.getString(3);
            Motion motion = parseMotion(cursor.getString(4));
            List<MuscleGroup> muscleGroups = parseMuscleGroups(cursor.getString(5));
            Equipment equipment = parseEquipment(cursor.getString(6));
            boolean isCustom = cursor.getInt(7) == 1;
            int position = cursor.getInt(8);

            ExerciseInBlock exercise = new ExerciseInBlock(linkId, exerciseId, name, description, motion, muscleGroups, equipment, position);
            exercise.setCustom(isCustom);
            exercises.add(exercise);
        }

        cursor.close();
        db.close();
        return exercises;
    }


    /**
     * Оновлює список вправ у блоці (перезаписує повністю).
     */
    public void updateTrainingBlockExercises(long blockId, List<ExerciseInBlock> exerciseInBlocks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete("TrainingBlockExercises", "trainingBlockId = ?", new String[]{String.valueOf(blockId)});

            ContentValues values = new ContentValues();
            for (ExerciseInBlock ex : exerciseInBlocks) {
                values.clear();
                values.put("trainingBlockId", blockId);
                values.put("exerciseId", ex.getId());
                values.put("position", ex.getPosition());
                db.insert("TrainingBlockExercises", null, values);
            }

            db.setTransactionSuccessful();
            Log.d("DAO", "Оновлено вправи у TrainingBlock ID = " + blockId);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при оновленні вправ у блоці", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Додає одну вправу до блоку, уникаючи дублювання.
     */
    public void addExerciseToBlock(long blockId, long exerciseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Знаходимо наступну позицію для вправи в цьому блоці
        int nextPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM TrainingBlockExercises WHERE trainingBlockId = ?",
                new String[]{String.valueOf(blockId)}
        );
        if (cursor.moveToFirst()) {
            nextPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        ContentValues cv = new ContentValues();
        cv.put("trainingBlockId", blockId);
        cv.put("exerciseId", exerciseId);
        cv.put("position", nextPosition);

        db.insertWithOnConflict("TrainingBlockExercises", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }


    /**
     * Видаляє вправу з блоку (за position).
     */
    public void removeExerciseFromBlock(long blockId, long exerciseId, int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("TrainingBlockExercises",
                "trainingBlockId=? AND exerciseId=? AND position=?",
                new String[]{String.valueOf(blockId), String.valueOf(exerciseId), String.valueOf(position)});
        db.close();
    }

// ---------------------------------------------------------
//         Парсери ENUM (Motion, MuscleGroup, Equipment)
// ---------------------------------------------------------

    private Motion parseMotion(String str) {
        if (str != null && !str.isEmpty()) {
            try { return Motion.valueOf(str); } catch (Exception ignored) {}
        }
        return null;
    }

    private Equipment parseEquipment(String str) {
        if (str != null && !str.isEmpty()) {
            try { return Equipment.valueOf(str); } catch (Exception ignored) {}
        }
        return null;
    }

    private List<MuscleGroup> parseMuscleGroups(String str) {
        List<MuscleGroup> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            for (String part : str.split(",")) {
                try { list.add(MuscleGroup.valueOf(part.trim())); } catch (Exception ignored) {}
            }
        }
        return list;
    }



// ------------------------------------------------------
//      КЛОНУВАННЯ ПРОГРАМИ ТРЕНУВАНЬ (глибоке)
// ------------------------------------------------------

    /**
     * Запускає процес клонування програми (FitnessProgram) з усіма вкладеннями:
     * GymSession (дні), TrainingBlock (блоки), фільтри (motion/muscle/equipment), вправи (ExerciseInBlock).
     */
    public FitnessProgram onStartCloneFitProgram(FitnessProgram programToClone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Клонуємо саму програму (безпосередньо)
            FitnessProgram clonedProgram = cloneProgram(programToClone, db);

            db.setTransactionSuccessful();
            return clonedProgram;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при клонуванні програми", e);
            return null;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Клонує саму програму (PlanCycles), присвоює нову позицію та ID.
     * Далі рекурсивно клонує всі GymSession, TrainingBlock і т.д.
     */
    private FitnessProgram cloneProgram(FitnessProgram program, SQLiteDatabase db) {
        // 1) Обчислюємо нову position
        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM PlanCycles",
                null
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        // 2) Створюємо об'єкт-програму з новими полями
        //    (при бажанні можна додати суфікс " (C)" у назві)
        FitnessProgram clone = new FitnessProgram(
                0,
                program.getName() + " (C)",
                program.getDescription(),
                new ArrayList<>() // дні додамо пізніше
        );

        // 3) Додаємо в базу
        ContentValues values = new ContentValues();
        values.put("name",         clone.getName());
        values.put("description",  clone.getDescription());
        values.put("creation_date", System.currentTimeMillis());
        values.put("position",     newPosition);
        values.put("is_active",    0);

        long newId = db.insert("PlanCycles", null, values);
        if (newId == -1) {
            throw new SQLException("Не вдалося вставити клон програми в PlanCycles");
        }
        clone.setId(newId);

        // 4) Клонуємо всі GymSession
        List<GymSession> clonedSessions = new ArrayList<>();
        for (GymSession session : program.getGymSessions()) {
            clonedSessions.add(cloneGymSession(session, newId, db));
        }
        clone.setGymSessions(clonedSessions);

        return clone;
    }

//
//  КЛОНУВАННЯ ОКРЕМОГО ДНЯ (GYMSESSION)
//

    /**
     * Клонує окремий GymSession разом із блоками, викликається під час клонування програми
     * або окремо через onStartCloneGymSession(...).
     */
    private GymSession cloneGymSession(GymSession session, long newProgramId, SQLiteDatabase db) {
        // 1) Знаходимо нову position серед GymDays, що належать planId = newProgramId
        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM GymDays WHERE plan_id = ?",
                new String[]{String.valueOf(newProgramId)}
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        // 2) Створюємо новий об'єкт GymSession
        //    (змінюємо назву, додаємо суфікс "(C)" за бажанням)
        GymSession clone = new GymSession(
                0,
                (int) newProgramId,
                session.getName() + " (C)",
                session.getDescription(),
                newPosition,
                new ArrayList<>() // блоки додамо після вставки
        );

        // 3) Додаємо день у таблицю GymDays
        ContentValues values = new ContentValues();
        values.put("plan_id",    clone.getPlanId());
        values.put("day_name",   clone.getName());    // "Some day (C)"
        values.put("description",clone.getDescription());
        values.put("position",   clone.getPosition());

        long newId = db.insert("GymDays", null, values);
        if (newId == -1) {
            throw new SQLException("Помилка вставки дня при клонуванні GymSession");
        }
        clone.setId(newId);

        // 4) Клонуємо блоки цього дня
        List<TrainingBlock> newBlocks = new ArrayList<>();
        for (TrainingBlock block : session.getTrainingBlocks()) {
            newBlocks.add(cloneTrainingBlock(block, newId, db));
        }
        clone.setTrainingBlocks(newBlocks);

        return clone;
    }

    /**
     * Запускає транзакцію клонування для окремого GymSession
     * (без клонування всієї програми).
     */
    public GymSession onStartCloneGymSession(GymSession gymSession) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            GymSession clone = cloneGymSession(gymSession, gymSession.getPlanId(), db);
            db.setTransactionSuccessful();
            return clone;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при клонуванні GymSession", e);
            return null;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

//
//  КЛОНУВАННЯ БЛОКУ (TRAININGBLOCK)
//
    /**
     * Клонує блок (TrainingBlock) із фільтрами та вправами.
     * Викликається з cloneGymSession(...) або окремо через onStartCloneTrainingBlock(...).
     */
    private TrainingBlock cloneTrainingBlock(TrainingBlock block, long newDayId, SQLiteDatabase db) {
        // 1) Знаходимо нову позицію в рамках TrainingBlock, де gym_day_id = newDayId
        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM TrainingBlock WHERE gym_day_id = ?",
                new String[]{String.valueOf(newDayId)}
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        // 2) Створюємо клонований блок
        TrainingBlock clone = new TrainingBlock(
                0,
                newDayId,
                block.getName() + " (C)",
                block.getDescription(),
                new ArrayList<>(block.getMotions()),           // нове: додаємо список фільтрів
                new ArrayList<>(block.getMuscleGroupList()),
                new ArrayList<>(block.getEquipmentList()),     // нове: додаємо список фільтрів
                newPosition,
                new ArrayList<>()
        );

        // 3) Вставляємо його в базу
        ContentValues values = new ContentValues();
        values.put("gym_day_id",  clone.getGymDayId());
        values.put("name",        clone.getName());
        values.put("description", clone.getDescription());
        values.put("position",    clone.getPosition());

        long newId = db.insert("TrainingBlock", null, values);
        if (newId == -1) {
            throw new SQLException("Помилка вставки нового TrainingBlock при клонуванні");
        }
        clone.setId(newId);

        // 4) Клонуємо фільтри (motion, muscleGroup, equipment)
        // Motion
        for (Motion motion : block.getMotions()) {
            ContentValues motionValues = new ContentValues();
            motionValues.put("trainingBlockId", newId);
            motionValues.put("motionType", motion.name());
            db.insert("TrainingBlockMotion", null, motionValues);
        }

        // Muscle Groups
        for (MuscleGroup muscle : block.getMuscleGroupList()) {
            ContentValues muscleValues = new ContentValues();
            muscleValues.put("trainingBlockId", newId);
            muscleValues.put("muscleGroup", muscle.name());
            db.insert("TrainingBlockMuscleGroup", null, muscleValues);
        }

        // Equipment
        for (Equipment equipment : block.getEquipmentList()) {
            ContentValues equipmentValues = new ContentValues();
            equipmentValues.put("trainingBlockId", newId);
            equipmentValues.put("equipment", equipment.name());
            db.insert("TrainingBlockEquipment", null, equipmentValues);
        }

        // 5) Клонуємо вправи (ExerciseInBlock)
        List<ExerciseInBlock> newExercises = new ArrayList<>();
        for (ExerciseInBlock ex : block.getExercises()) {
            newExercises.add(cloneExerciseInBlock(ex, newId, db));
        }
        clone.setExercises(newExercises);

        return clone;
    }


    /**
     * Окрема транзакція клонування блоків (без днів і планів).
     */
    public TrainingBlock onStartCloneTrainingBlock(TrainingBlock block) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            TrainingBlock clone = cloneTrainingBlock(block, block.getGymDayId(), db);
            db.setTransactionSuccessful();
            return clone;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Помилка при клонуванні TrainingBlock", e);
            return null;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

//
//  КЛОНУВАННЯ ФІЛЬТРІВ ТА ВПРАВ
//

    /**
     * Копіює motion, muscleGroup, equipment з одного блока в інший.
     */
    private void cloneBlockFilters(long sourceBlockId, long targetBlockId, SQLiteDatabase db) {
        cloneFilter("motionType",      "TrainingBlockMotion",      sourceBlockId, targetBlockId, db);
        cloneFilter("muscleGroup",     "TrainingBlockMuscleGroup", sourceBlockId, targetBlockId, db);
        cloneFilter("equipment",       "TrainingBlockEquipment",   sourceBlockId, targetBlockId, db);
    }

    private void cloneFilter(String column, String table, long fromBlockId, long toBlockId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT " + column + " FROM " + table + " WHERE trainingBlockId = ?",
                new String[]{String.valueOf(fromBlockId)}
        );
        while (cursor.moveToNext()) {
            String value = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put("trainingBlockId", toBlockId);
            values.put(column, value);
            db.insert(table, null, values);
        }
        cursor.close();
    }

    /**
     * Клонує вправу в блоці (ExerciseInBlock) з тією ж логікою, але новою position.
     */
    private ExerciseInBlock cloneExerciseInBlock(ExerciseInBlock exerciseInBlock, long newBlockId, SQLiteDatabase db) {
        // 1) Визначити нову позицію
        int newPosition = 0;
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(position), -1) + 1 AS nextPos FROM TrainingBlockExercises WHERE trainingBlockId = ?",
                new String[]{String.valueOf(newBlockId)}
        );
        if (cursor.moveToFirst()) {
            newPosition = cursor.getInt(cursor.getColumnIndexOrThrow("nextPos"));
        }
        cursor.close();

        // 2) Створити клон
        ExerciseInBlock clone = new ExerciseInBlock(
                exerciseInBlock.getLinkId(),
                exerciseInBlock.getId(),
                exerciseInBlock.getName(),
                exerciseInBlock.getDescription(), // <--- нове
                exerciseInBlock.getMotion(),
                exerciseInBlock.getMuscleGroupList(),
                exerciseInBlock.getEquipment(),
                newPosition
        );

        // 3) Вставити у TrainingBlockExercises
        ContentValues values = new ContentValues();
        values.put("trainingBlockId", newBlockId);
        values.put("exerciseId",      clone.getId());
        values.put("position",        clone.getPosition());

        long inserted = db.insert("TrainingBlockExercises", null, values);
        if (inserted == -1) {
            throw new SQLException("Помилка вставки вправи при клонуванні ExerciseInBlock");
        }

        return clone;
    }



    /**
     * Логує всю структуру:
     *  1. Усі плани (PlanCycles)
     *  2. Кожен день (GymDay) в плані
     *  3. Кожен блок (TrainingBlock) у дні
     *  4. Кожну вправу в блоці (TrainingBlockExercises -> Exercise)
     */
    public void logAllData() {
        Log.d("logAllData", "========== START: logAllData() ==========");

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursorPlans = db.rawQuery("SELECT id, name, description FROM PlanCycles", null)) {

            while (cursorPlans.moveToNext()) {
                long planId = cursorPlans.getLong(0);
                String planName = cursorPlans.getString(1);
                String planDesc = cursorPlans.getString(2);

                Log.d("logAllData",
                        "PlanCycle [id=" + planId + ", name=" + planName + ", desc=" + planDesc + "]"
                );

                // ----- GymDays -----
                try (Cursor cursorDays = db.rawQuery(
                        "SELECT id, day_name, description FROM GymDays WHERE plan_id = ?",
                        new String[]{String.valueOf(planId)}
                )) {
                    while (cursorDays.moveToNext()) {
                        long dayId = cursorDays.getLong(0);
                        String dayName = cursorDays.getString(1);
                        String dayDesc = cursorDays.getString(2);

                        Log.d("logAllData",
                                "  GymDay [id=" + dayId + ", name=" + dayName + ", desc=" + dayDesc + "]"
                        );

                        // ----- TrainingBlocks -----
                        try (Cursor cursorBlocks = db.rawQuery(
                                "SELECT id, name, description FROM TrainingBlock WHERE gym_day_id = ?",
                                new String[]{String.valueOf(dayId)}
                        )) {
                            while (cursorBlocks.moveToNext()) {
                                long blockId = cursorBlocks.getLong(0);
                                String blockName = cursorBlocks.getString(1);
                                String blockDesc = cursorBlocks.getString(2);

                                Log.d("logAllData",
                                        "    TrainingBlock [id=" + blockId + ", name=" + blockName + ", desc=" + blockDesc + "]"
                                );

                                // ----- Exercises in block -----
                                try (Cursor cursorExercises = db.rawQuery(
                                        "SELECT tbe.exerciseId, e.name, e.description, tbe.position " +
                                                "FROM TrainingBlockExercises tbe " +
                                                "JOIN Exercise e ON e.id = tbe.exerciseId " +
                                                "WHERE tbe.trainingBlockId = ?",
                                        new String[]{String.valueOf(blockId)}
                                )) {
                                    while (cursorExercises.moveToNext()) {
                                        long exId = cursorExercises.getLong(0);
                                        String exName = cursorExercises.getString(1);
                                        String exDesc = cursorExercises.getString(2);
                                        int position = cursorExercises.getInt(3);

                                        Log.d("logAllData",
                                                "      Exercise [id=" + exId + ", name=" + exName + ", desc=" + exDesc + ", position=" + position + "]"
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Log.d("logAllData", "========== END: logAllData() ==========");
    }


}

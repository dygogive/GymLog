package com.example.gymlog.data.local.legacy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {

    // Версія бази (збільшено до 7 через видалення таблиць ExercisesGroups і ExercisesGroupExercises)
    private static final String DATABASE_NAME = "GymLog.db";
    public static final int VERSION = 22;
    private final Context context;

    // Конструктор
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    // Створення таблиць
    /*Розбір кожного елемента:
id INTEGER PRIMARY KEY AUTOINCREMENT
→ Унікальний ідентифікатор кожного дня тренувань.

plan_id INTEGER NOT NULL
→ Посилання на програму тренувань (PlanCycles), до якої належить цей день.
→ INTEGER NOT NULL означає, що значення обов’язкове (день обов’язково належить до певної програми).

day_name TEXT NOT NULL
→ Назва дня тренувань (наприклад, "День грудей та трицепсу").
→ TEXT NOT NULL означає, що значення обов’язкове.

description TEXT
→ Опис цього дня (наприклад, "Фокус на жимові вправи").
→ TEXT (може бути порожнім).

FOREIGN KEY (plan_id) REFERENCES PlanCycles(id) ON DELETE CASCADE
→ plan_id є зовнішнім ключем (Foreign Key) і посилається на поле id таблиці PlanCycles.
→ ON DELETE CASCADE означає, що якщо запис у PlanCycles буде видалений, то всі GymDays, пов’язані з ним, теж видаляються автоматично.*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Exercise table remains the same
        db.execSQL("CREATE TABLE IF NOT EXISTS Exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "motion TEXT, " +
                "muscleGroups TEXT, " +
                "equipment TEXT, " +
                "isCustom INTEGER DEFAULT 0)");

        // PlanCycles remains the same
        db.execSQL("CREATE TABLE IF NOT EXISTS PlanCycles (" +
                "    id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name         TEXT    NOT NULL," +
                "    description  TEXT," +
                "    creation_date TEXT," +
                "    position     INTEGER," +
                "    is_active    INTEGER DEFAULT 0," +
                "    uuid         TEXT  NOT NULL" +
                ");");

        // GymDays remains the same
        db.execSQL("CREATE TABLE IF NOT EXISTS GymDays (" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    plan_id     INTEGER NOT NULL," +
                "    day_name    TEXT    NOT NULL," +
                "    description TEXT," +
                "    position    INTEGER," +
                "    FOREIGN KEY (plan_id) REFERENCES PlanCycles(id) ON DELETE CASCADE" +
                ");");

        // TrainingBlock remains the same
        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlock (" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    gym_day_id  INTEGER NOT NULL," +
                "    name        TEXT    NOT NULL," +
                "    description TEXT," +
                "    position    INTEGER," +
                "    uuid        TEXT  NOT NULL," +
                "    FOREIGN KEY (gym_day_id) REFERENCES GymDays(id) ON DELETE CASCADE" +
                ");");

        // Filter tables remain the same
        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlockMotion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "motionType TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlockMuscleGroup (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "muscleGroup TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlockEquipment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "equipment TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        // TrainingBlockExercises remains the same
        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlockExercises (" +
                "        id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                "        trainingBlockId INTEGER," +
                "        exerciseId      INTEGER," +
                "        position        INTEGER," +
                "        FOREIGN KEY(trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE," +
                "        FOREIGN KEY(exerciseId)      REFERENCES Exercise(id)      ON DELETE CASCADE)");


        // Updated workout_result table
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_result (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "programUuid TEXT NOT NULL, " +
                "trainingBlockUuid TEXT, " +
                "exerciseId INTEGER NOT NULL, " +
                "weight INTEGER, " +
                "iteration INTEGER, " +
                "workTime INTEGER, " +
                "sequenceInGymDay INTEGER NOT NULL, " +
                "position INTEGER NOT NULL, " +
                "timeFromStart INTEGER NOT NULL, " +
                "workoutDateTime TEXT NOT NULL, " +  // Додано нову колонку
                "FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE" +
                ");");

        // Create indexes
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMotion_trainingBlockId ON TrainingBlockMotion(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMuscleGroup_trainingBlockId ON TrainingBlockMuscleGroup(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockEquipment_trainingBlockId ON TrainingBlockEquipment(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_trainingBlockId ON TrainingBlockExercises(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_exerciseId ON TrainingBlockExercises(exerciseId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlock_gym_day_id ON TrainingBlock(gym_day_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_GymDays_plan_id ON GymDays(plan_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_programUuid ON workout_result(programUuid);");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_trainingBlockUuid ON workout_result(trainingBlockUuid);");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseId ON workout_result(exerciseId);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 22) {
            Log.d("onUpgradeCheck", "Message: " + 1 );
            // 1. Перевіряємо та додаємо колонку uuid до PlanCycles, якщо її немає
            if (!isColumnExists(db, "PlanCycles", "uuid")) {
                Log.d("onUpgradeCheck", "Message: " + 2 );

                db.execSQL("ALTER TABLE PlanCycles ADD COLUMN uuid TEXT NOT NULL DEFAULT ''");

                // Генеруємо UUID для існуючих записів
                Cursor planCursor = db.rawQuery("SELECT id FROM PlanCycles", null);
                if (planCursor != null) {
                    try {
                        while (planCursor.moveToNext()) {
                            int id = planCursor.getInt(0);
                            String uuid = UUID.randomUUID().toString();
                            db.execSQL("UPDATE PlanCycles SET uuid = ? WHERE id = ?",
                                    new Object[]{uuid, id});
                        }
                    } finally {
                        planCursor.close();
                    }
                }
            }

            // 2. Перевіряємо та додаємо колонку uuid до TrainingBlock
            if (!isColumnExists(db, "TrainingBlock", "uuid")) {
                Log.d("onUpgradeCheck", "Message: " + 3 );
                db.execSQL("ALTER TABLE TrainingBlock ADD COLUMN uuid TEXT NOT NULL DEFAULT ''");

                // Генеруємо UUID для існуючих записів
                Cursor blockCursor = db.rawQuery("SELECT id FROM TrainingBlock", null);
                if (blockCursor != null) {
                    try {
                        while (blockCursor.moveToNext()) {
                            int id = blockCursor.getInt(0);
                            String uuid = UUID.randomUUID().toString();
                            db.execSQL("UPDATE TrainingBlock SET uuid = ? WHERE id = ?",
                                    new Object[]{uuid, id});
                        }
                    } finally {
                        blockCursor.close();
                    }
                }
            }

            // 3. Перевіряємо структуру workout_result
            if (!isTableExists(db, "workout_result") ||
                    !isColumnExists(db, "workout_result", "programUuid")) {

                Log.d("onUpgradeCheck", "Message: " + 4 );

                // Створюємо нову таблицю
                db.execSQL("CREATE TABLE IF NOT EXISTS workout_result_new (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "programUuid TEXT NOT NULL, " +
                        "trainingBlockUuid TEXT, " +
                        "exerciseId INTEGER NOT NULL, " +
                        "weight INTEGER, " +
                        "iteration INTEGER, " +
                        "workTime INTEGER, " +
                        "sequenceInGymDay INTEGER NOT NULL, " +
                        "position INTEGER NOT NULL, " +
                        "timeFromStart INTEGER NOT NULL, " +
                        "workoutDateTime TEXT NOT NULL, " +
                        "FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE);");

                // Переносимо дані, якщо стара таблиця існує
                if (isTableExists(db, "workout_result")) {

                    try {
                        db.execSQL("INSERT INTO workout_result_new (" +
                                "id, exerciseId, weight, iteration, workTime, sequenceInGymDay, " +
                                "position, timeFromStart, workoutDateTime, programUuid, trainingBlockUuid) " +
                                "SELECT wr.id, tbe.exerciseId, wr.weight, wr.iteration, wr.workTime, " +
                                "wr.sequenceInGymDay, wr.position, wr.timeFromStart, wr.workoutDateTime, " +
                                "pc.uuid, tb.uuid " +
                                "FROM workout_result wr " +
                                "JOIN TrainingBlockExercises tbe ON wr.exerciseInBlockId = tbe.id " +
                                "JOIN TrainingBlock tb ON tbe.trainingBlockId = tb.id " +
                                "JOIN GymDays gd ON tb.gym_day_id = gd.id " +
                                "JOIN PlanCycles pc ON gd.plan_id = pc.id");
                    } catch (Exception e) {
                        // Якщо помилка міграції даних - продовжуємо з порожньою таблицею
                    }

                    db.execSQL("DROP TABLE workout_result");
                }

                db.execSQL("ALTER TABLE workout_result_new RENAME TO workout_result");

                // Створюємо індекси
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_programUuid ON workout_result(programUuid)");
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_trainingBlockUuid ON workout_result(trainingBlockUuid)");
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseId ON workout_result(exerciseId)");
            }
        } else {
            Log.d("onUpgradeCheck", "Message: " + 5 );
            // Якщо версія вже 22+, просто перевіряємо цілісність БД
            try {
                // Швидка перевірка доступу до ключових таблиць
                db.rawQuery("SELECT 1 FROM PlanCycles LIMIT 1", null).close();
                db.rawQuery("SELECT 1 FROM workout_result LIMIT 1", null).close();
            } catch (Exception e) {
                // У разі проблем - перестворюємо БД
                recreateDatabase(db);
            }
            Log.d("onUpgradeCheck", "Message: " + 6 );
        }
    }

    // Допоміжні методи
    private boolean isColumnExists(SQLiteDatabase db, String table, String column) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            while (cursor != null && cursor.moveToNext()) {
                if (cursor.getString(1).equals(column)) {
                    return true;
                }
            }
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean isTableExists(SQLiteDatabase db, String table) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'",
                    null
            );
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void recreateDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS Workout");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        db.execSQL("DROP TABLE IF EXISTS Exercise");
        db.execSQL("DROP TABLE IF EXISTS PlanCycles");
        db.execSQL("DROP TABLE IF EXISTS GymDays");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlock");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockMotion");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockMuscleGroup");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockEquipment");
        db.execSQL("DROP TABLE IF EXISTS workout_result");
        onCreate(db);
    }
}

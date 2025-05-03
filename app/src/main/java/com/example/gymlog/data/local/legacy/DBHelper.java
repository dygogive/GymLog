package com.example.gymlog.data.local.legacy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // Версія бази (збільшено до 7 через видалення таблиць ExercisesGroups і ExercisesGroupExercises)
    private static final String DATABASE_NAME = "GymLog.db";
    public static final int VERSION = 21;
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
                "    is_active    INTEGER DEFAULT 0" +
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
                "exerciseInBlockId INTEGER NOT NULL, " +
                "weight INTEGER, " +
                "iteration INTEGER, " +
                "workTime INTEGER, " +
                "sequenceInGymDay INTEGER NOT NULL, " +
                "position INTEGER NOT NULL, " +
                "timeFromStart INTEGER NOT NULL, " +
                "workoutDateTime TEXT NOT NULL, " +  // Додано нову колонку
                "FOREIGN KEY (exerciseInBlockId) REFERENCES TrainingBlockExercises(id) ON DELETE CASCADE" +
                ");");

        // Create indexes
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMotion_trainingBlockId ON TrainingBlockMotion(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMuscleGroup_trainingBlockId ON TrainingBlockMuscleGroup(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockEquipment_trainingBlockId ON TrainingBlockEquipment(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_trainingBlockId ON TrainingBlockExercises(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_exerciseId ON TrainingBlockExercises(exerciseId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlock_gym_day_id ON TrainingBlock(gym_day_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_GymDays_plan_id ON GymDays(plan_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseInBlockId ON workout_result(exerciseInBlockId);");
    }


    // Оновлення таблиць
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Workout");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        db.execSQL("DROP TABLE IF EXISTS Exercise");
        db.execSQL("DROP TABLE IF EXISTS PlanCycles");
        db.execSQL("DROP TABLE IF EXISTS GymDays");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlock"); // Видалена ExercisesGroups, тепер TrainingBlock
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockMotion");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockMuscleGroup");
        db.execSQL("DROP TABLE IF EXISTS TrainingBlockEquipment");
        db.execSQL("DROP TABLE IF EXISTS workout_result");
        onCreate(db);
    }
}

package com.example.gymlog.data.local.legacy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // Версія бази (збільшено до 7 через видалення таблиць ExercisesGroups і ExercisesGroupExercises)
    private static final String DATABASE_NAME = "GymLog.db";
    public static final int VERSION = 19;
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



        // Вправи
        db.execSQL("CREATE TABLE IF NOT EXISTS Exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "motion TEXT, " +
                "muscleGroups TEXT, " +
                "equipment TEXT, " +
                "isCustom INTEGER DEFAULT 0)");

        // Програми (PlanCycles)
        db.execSQL("CREATE TABLE IF NOT EXISTS PlanCycles (\n" +
                "    id           INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name         TEXT    NOT NULL,\n" +
                "    description  TEXT,\n" +
                "    creation_date TEXT,                 -- прибрали NOT NULL\n" +
                "    position     INTEGER,\n" +
                "    is_active    INTEGER DEFAULT 0                -- прибрали DEFAULT 0\n" +
                ");\n");

        // Дні (GymDays)
        db.execSQL("CREATE TABLE IF NOT EXISTS GymDays (\n" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    plan_id     INTEGER NOT NULL,\n" +
                "    day_name    TEXT    NOT NULL,\n" +
                "    description TEXT,\n" +
                "    position    INTEGER,\n" +
                "    FOREIGN KEY (plan_id) REFERENCES PlanCycles(id) ON DELETE CASCADE\n" +
                ");");

        // Блоки (TrainingBlock)
        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlock (\n" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    gym_day_id  INTEGER NOT NULL,\n" +
                "    name        TEXT    NOT NULL,\n" +
                "    description TEXT,\n" +
                "    position    INTEGER,\n" +
                "    FOREIGN KEY (gym_day_id) REFERENCES GymDays(id) ON DELETE CASCADE\n" +
                ");");

        // Фільтри блоку (motion / muscle / equipment)
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

        // **Нова таблиця** зв’язку "TrainingBlock -> Exercise"
        db.execSQL("CREATE TABLE IF NOT EXISTS TrainingBlockExercises (\n" +
                "        id              INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "        trainingBlockId INTEGER,      -- було  ... NOT NULL\n" +
                "        exerciseId      INTEGER,      -- було  ... NOT NULL\n" +
                "        position        INTEGER,\n" +
                "        FOREIGN KEY(trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE,\n" +
                "        FOREIGN KEY(exerciseId)      REFERENCES Exercise(id)      ON DELETE CASCADE)");

        // WorkoutExercises - додаємо NOT NULL обмеження згідно Entity
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutExercises (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    workout_gymday_ID INTEGER,\n" +
                "    exerciseId INTEGER,\n" +
                "    name TEXT NOT NULL,\n" +                     // додано NOT NULL
                "    description TEXT,\n" +
                "    motion TEXT NOT NULL,\n" +                   // додано NOT NULL
                "    muscleGroups TEXT NOT NULL,\n" +             // додано NOT NULL
                "    equipment TEXT NOT NULL,\n" +                // додано NOT NULL
                "    comments TEXT,\n" +                          // коментарі
                "    FOREIGN KEY (workout_gymday_ID) REFERENCES WorkoutGymDay(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE SET NULL\n" +
                ");\n");

        // WorkoutExercises - додаємо NOT NULL обмеження згідно Entity
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_result (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  workoutExerciseId INTEGER NOT NULL,\n" +      // зробили NOT NULL
                "  weight INTEGER,\n" +
                "  iteration INTEGER,\n" +
                "  workTime INTEGER,\n" +                        // змінили назву колонки на CamelCase
                "  orderInWorkoutExercise INTEGER NOT NULL,\n" +
                "  orderInWorkSet INTEGER NOT NULL,\n" +
                "  orderInWorkGymDay INTEGER NOT NULL,\n" +
                "  minutesSinceStartWorkout INTEGER NOT NULL,\n" +
                "  date TEXT NOT NULL,\n" +
                "  FOREIGN KEY (workoutExerciseId) REFERENCES WorkoutExercises(id) ON DELETE CASCADE\n" +
                ");");

// WorkoutGymDay - додаємо NOT NULL обмеження згідно Entity
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutGymDay (\n" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    datetime    INTEGER NOT NULL,\n" +           // додано NOT NULL
                "    plansID     INTEGER,\n" +
                "    gymDaysID   INTEGER,\n" +
                "    sets        INTEGER NOT NULL,\n" +           // додано NOT NULL
                "    blocks      INTEGER NOT NULL,\n" +           // додано NOT NULL
                "    minutes     INTEGER,\n" +
                "    name        TEXT NOT NULL,\n" +              // додано NOT NULL
                "    description TEXT,\n" +
                "    physicalСondition INTEGER,\n" +
                "    comments    TEXT,\n" +
                "    FOREIGN KEY (plansID)  REFERENCES PlanCycles(id) ON DELETE SET NULL,\n" +
                "    FOREIGN KEY (gymDaysID) REFERENCES GymDays(id) ON DELETE SET NULL\n" +
                ");");

// WorkoutSet - додаємо NOT NULL обмеження згідно Entity
        db.execSQL("CREATE TABLE IF NOT EXISTS WorkoutSet (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    workout_id INTEGER,\n" +
                "    tr_block_id INTEGER,\n" +
                "    name TEXT NOT NULL,\n" +                     // додано NOT NULL
                "    description TEXT,\n" +
                "    position INTEGER NOT NULL,\n" +              // додано NOT NULL
                "    physicalСondition INTEGER,\n" +
                "    comments TEXT,\n" +
                "    FOREIGN KEY (workout_id) REFERENCES WorkoutGymDay(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (tr_block_id) REFERENCES TrainingBlock(id) ON DELETE SET NULL\n" +
                ");\n");


        // ───── ІНДЕКСИ, які Room очікує бачити ──────────────────────────



// TrainingBlockMotion / MuscleGroup / Equipment
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMotion_trainingBlockId       ON TrainingBlockMotion(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockMuscleGroup_trainingBlockId  ON TrainingBlockMuscleGroup(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockEquipment_trainingBlockId    ON TrainingBlockEquipment(trainingBlockId)");

// TrainingBlockExercises
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_trainingBlockId ON TrainingBlockExercises(trainingBlockId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlockExercises_exerciseId     ON TrainingBlockExercises(exerciseId)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_TrainingBlock_gym_day_id ON TrainingBlock(gym_day_id)");

// WorkoutGymDay
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutGymDay_plansID   ON WorkoutGymDay(plansID)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutGymDay_gymDaysID ON WorkoutGymDay(gymDaysID)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_GymDays_plan_id ON GymDays(plan_id)");

// WorkoutSet
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutSet_workout_id ON WorkoutSet(workout_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutSet_tr_block_id ON WorkoutSet(tr_block_id)");

// WorkoutExercises
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutExercises_workout_gymday_ID ON WorkoutExercises(workout_gymday_ID)");
        db.execSQL("CREATE INDEX IF NOT EXISTS index_WorkoutExercises_exerciseId        ON WorkoutExercises(exerciseId)");

// workout_result
        db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_workoutExerciseId " +
                "ON workout_result(workoutExerciseId);");
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
        db.execSQL("DROP TABLE IF EXISTS WorkoutGymDay");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        db.execSQL("DROP TABLE IF EXISTS WorkoutExercises");
        db.execSQL("DROP TABLE IF EXISTS workout_result");
        onCreate(db);
    }
}

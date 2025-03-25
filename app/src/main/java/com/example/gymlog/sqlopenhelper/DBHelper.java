package com.example.gymlog.sqlopenhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    // Версія бази (збільшено до 7 через видалення таблиць ExercisesGroups і ExercisesGroupExercises)
    private static final String DATABASE_NAME = "GymLog.db";
    private static final int version = 14;
    private final Context context;

    // Конструктор
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, version);
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
        db.execSQL("CREATE TABLE Exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "motion TEXT, " +
                "muscleGroups TEXT, " +
                "equipment TEXT, " +
                "isCustom INTEGER DEFAULT 0)");

        // Програми (PlanCycles)
        db.execSQL("CREATE TABLE PlanCycles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "creation_date TEXT NOT NULL, " +
                "position INTEGER, " +
                "is_active INTEGER DEFAULT 0)");

        // Дні (GymDays)
        db.execSQL("CREATE TABLE GymDays (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "plan_id INTEGER NOT NULL, " +
                "day_name TEXT NOT NULL, " +
                "description TEXT, " +
                "position INTEGER, " +
                "FOREIGN KEY (plan_id) REFERENCES PlanCycles(id) ON DELETE CASCADE)");

        // Блоки (TrainingBlock)
        db.execSQL("CREATE TABLE TrainingBlock (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "gym_day_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "position INTEGER, " +
                "FOREIGN KEY (gym_day_id) REFERENCES GymDays(id) ON DELETE CASCADE)");

        // Фільтри блоку (motion / muscle / equipment)
        db.execSQL("CREATE TABLE TrainingBlockMotion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "motionType TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE TrainingBlockMuscleGroup (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "muscleGroup TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE TrainingBlockEquipment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "trainingBlockId INTEGER, " +
                "equipment TEXT, " +
                "FOREIGN KEY (trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE)");

        // **Нова таблиця** зв’язку "TrainingBlock -> Exercise"
        db.execSQL("CREATE TABLE TrainingBlockExercises (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    trainingBlockId INTEGER NOT NULL,\n" +
                "    exerciseId INTEGER NOT NULL,\n" +
                "    position INTEGER,\n" +
                "    FOREIGN KEY(trainingBlockId) REFERENCES TrainingBlock(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY(exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE)");

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
        onCreate(db);
    }
}

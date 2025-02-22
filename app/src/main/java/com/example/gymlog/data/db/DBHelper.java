package com.example.gymlog.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {


    //Версія бази
    private static final String DATABASE_NAME = "GymLog.db";
    private static final int version = 5;

    //конструктор
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    //створення таблиць
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("CREATE TABLE Workout (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT," +
                " date TEXT)");


        db.execSQL("CREATE TABLE WorkoutSet (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " workoutId INTEGER," +
                " exercise TEXT," +
                " reptype TEXT," +
                " weight REAL," +
                " reps INTEGER)");


        //вправи
        db.execSQL("CREATE TABLE Exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "motion TEXT, " +
                "muscleGroups TEXT, " +
                "equipment TEXT, " +
                "isCustom INTEGER DEFAULT 0" + //  0: вбудована вправа, 1: створена користувачем
                ");");


        // Створення таблиць для планів
        db.execSQL("CREATE TABLE PlanCycles (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name TEXT NOT NULL,\n" +
                "    description TEXT,\n" +
                "    creation_date TEXT NOT NULL,\n" +
                "    is_active INTEGER DEFAULT 0\n" +
                ");");

        db.execSQL("CREATE TABLE GymDays (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    plan_id INTEGER NOT NULL,\n" +
                "    day_order INTEGER NOT NULL,\n" +
                "    description TEXT,\n" +
                "    FOREIGN KEY (plan_id) REFERENCES PlanCycles(id) ON DELETE CASCADE\n" +
                ");");

        db.execSQL("CREATE TABLE ExercisesGroups (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    gym_day_id INTEGER NOT NULL,\n" +
                "    name TEXT NOT NULL,\n" +
                "    description TEXT,\n" +
                "    priority INTEGER NOT NULL CHECK (priority IN (1, 2, 3)),\n" +
                "    FOREIGN KEY (gym_day_id) REFERENCES GymDays(id) ON DELETE CASCADE\n" +
                ");");

        db.execSQL("CREATE TABLE ExercisesGroupExercises (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    group_id INTEGER NOT NULL,\n" +
                "    exercise_id INTEGER NOT NULL,\n" +
                "    exercise_order INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (group_id) REFERENCES ExercisesGroups(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (exercise_id) REFERENCES Exercise(id)\n" +
                ");");
    }

    //оновлення таблиць
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Workout");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        db.execSQL("DROP TABLE IF EXISTS Exercise");
        db.execSQL("DROP TABLE IF EXISTS PlanCycles");
        db.execSQL("DROP TABLE IF EXISTS GymDays");
        db.execSQL("DROP TABLE IF EXISTS ExercisesGroups");
        db.execSQL("DROP TABLE IF EXISTS ExercisesGroupExercises");
        onCreate(db);
    }




}

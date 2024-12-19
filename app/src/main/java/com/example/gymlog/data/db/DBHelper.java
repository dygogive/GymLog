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
        db.execSQL("CREATE TABLE Workout (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT," +
                " date TEXT)");
        db.execSQL("CREATE TABLE WorkoutSet (id INTEGER PRIMARY KEY AUTOINCREMENT, workoutId INTEGER," +
                " exercise TEXT, reptype TEXT, weight REAL, reps INTEGER)");
        db.execSQL("CREATE TABLE Exercise (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "motion TEXT, " +
                "muscleGroups TEXT, " +
                "equipment TEXT, " +
                "isCustom INTEGER DEFAULT 0" + //  0: вбудована вправа, 1: створена користувачем
                ");");
    }

    //оновлення таблиць
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Workout");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        db.execSQL("DROP TABLE IF EXISTS Exercise");
        onCreate(db);
    }


}

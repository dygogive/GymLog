package com.example.gymlog.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WorkoutDAO {
    private SQLiteDatabase database;

    public WorkoutDAO(SQLiteDatabase database) {
        this.database = database;
    }

    //додати сет в таблицю Workout
    public Long addWorkout(String date, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        return database.insert("Workout", null, values);
    }

    public Cursor getWorkouts() {
        return database.query("Workout", null, null, null, null, null, "date DESC");
    }
}

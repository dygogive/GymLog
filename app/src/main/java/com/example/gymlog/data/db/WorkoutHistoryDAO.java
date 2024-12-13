package com.example.gymlog.data.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkoutHistoryDAO {

    private SQLiteDatabase database;

    public WorkoutHistoryDAO(SQLiteDatabase database) {
        this.database = database;
    }


    //отримати історію тренувань
    public Cursor getWorkoutHistory(){

        return database.rawQuery("SELECT w.name, s.exercise, s.reptype, s.weight, s.reps," +
                        " w.date FROM Workout w INNER JOIN WorkoutSet s ON w.id = s.workoutId",
                null);
    }


    //показати таблиці в об'єднану таблицю в Log
    public void logJoinedTable(){
        Cursor cursor = getWorkoutHistory();

        try {
            if(cursor.moveToFirst()){
                do{
                    String name, exercise, reptype, weight, reps, date;
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    exercise = cursor.getString(cursor.getColumnIndexOrThrow("exercise"));
                    reptype = cursor.getString(cursor.getColumnIndexOrThrow("reptype"));
                    weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
                    reps = cursor.getString(cursor.getColumnIndexOrThrow("reps"));
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    //вивести в Log
                    Log.d("MyDB",name + " : " + exercise + " : " + reptype +
                            " : " + weight + " : " + reps + " : " + date);
                } while (cursor.moveToNext());
            } else Log.d("MyDB","no data");
        } catch (Exception e) {
            Log.d("MyDB","error");
        } finally {
            if (cursor != null) cursor.close();
        }

    }
}

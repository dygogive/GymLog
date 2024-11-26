package com.example.gymlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    //Версія бази
    private static final int version = 2;

    //конструктор
    public DBHelper(@Nullable Context context) {
        super(context, "GymLog.db", null, version);
    }

    //створення таблиць
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Workout   (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT," +
                " date TEXT)");
        db.execSQL("CREATE TABLE WorkoutSet(id INTEGER PRIMARY KEY AUTOINCREMENT, workoutId INTEGER," +
                " exercise TEXT, reptype TEXT, weight REAL, reps INTEGER)");
    }

    //оновлення таблиць
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Workout");
        db.execSQL("DROP TABLE IF EXISTS WorkoutSet");
        onCreate(db);
    }

    //додати сет в таблицю Workout
    Long addWorkout(String date, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        return this.getWritableDatabase().insert("Workout", null, values);
    }

    //додати сет в таблицю WorkoutSet
    void addWorkoutSet(Long workoutId, String exercise, String reptype, String weight, String reps) {
        ContentValues values = new ContentValues();
        values.put("workoutId", workoutId);
        values.put("exercise", exercise);
        values.put("reptype", reptype);
        values.put("weight", weight);
        values.put("reps", reps);
        this.getWritableDatabase().insert("WorkoutSet", null, values);
    }

    //додати дані в 2 таблиці - додати ціле тренування з сетами
    void addWorkoutWithSets(String date, String name, List<WorkoutSet> setList) {
        //Додати в таблицю Workout
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        long id = (Long) this.getWritableDatabase().insert("Workout", null, values);
        values.clear();

        //Додати в таблицю WorkoutSet
        for(WorkoutSet workoutSet : setList){
            values.clear();
            values.put("workoutId", id);
            values.put("exercise", workoutSet.getExercise());
            values.put("reptype", workoutSet.getRepType());
            values.put("weight", String.valueOf(workoutSet.getWeight()));
            values.put("reps", String.valueOf(workoutSet.getReps()));
            this.getWritableDatabase().insert("WorkoutSet", null, values);
        }

    }

    //отримати історію тренувань
    Cursor getWorkoutHistory(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT w.name, s.exercise, s.reptype, s.weight, s.reps," +
                " w.date FROM Workout w INNER JOIN WorkoutSet s ON w.id = s.workoutId",
                null);
    }


    //показати таблиці в об'єднану таблицю в Log
    public void logJoinedTable(){
        SQLiteDatabase database = this.getReadableDatabase();
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

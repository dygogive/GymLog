package com.example.gymlog.sqlopenhelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymlog.model.set.WorkoutSet;

import java.util.List;

public class WorkoutSetDAO {

    private SQLiteDatabase database;

    public WorkoutSetDAO(SQLiteDatabase database) {
        this.database = database;
    }



    //додати сет в таблицю WorkoutSet
    public void addWorkoutSet(Long workoutId, String exercise, String reptype, String weight, String reps) {
        ContentValues values = new ContentValues();
        values.put("workoutId", workoutId);
        values.put("exercise", exercise);
        values.put("reptype", reptype);
        values.put("weight", weight);
        values.put("reps", reps);
        database.insert("WorkoutSet", null, values);
    }

    //додати дані в 2 таблиці - додати ціле тренування з сетами
    public void addWorkoutWithSets(String date, String name, List<WorkoutSet> setList) {
        //Додати в таблицю Workout
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        long id = (Long) database.insert("Workout", null, values);
        values.clear();

        //Додати в таблицю WorkoutSet
        for(WorkoutSet workoutSet : setList){
            addWorkoutSet(
                    id,
                    workoutSet.getExercise(),
                    workoutSet.getRepType(),
                    String.valueOf(workoutSet.getWeight()),
                    String.valueOf(workoutSet.getReps())
            );
        }

    }
}

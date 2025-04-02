package com.example.gymlog.database

import androidx.room.*


interface ExerciseResultDao {

    //метод для вставки об'єкта
    @Insert
    suspend fun insert(result: ExerciseResult): Long

    //метод для взяття об'єктів з бази
    @Query("SELECT * FROM exercise_results ORDER BY timestamp DESC")
    suspend fun getAllResults(): List<ExerciseResult>

    //метод для оновлення об'єкта в базі
    @Update
    suspend fun update(result: ExerciseResult)

    //метод для видалення з бази
    @Delete
    suspend fun delete(result: ExerciseResult)

}
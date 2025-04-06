package com.example.gymlog.database.testroom

import androidx.room.*

@Dao
interface ExerciseResultDao {
    @Insert
    suspend fun insert(exercise: ExerciseResult): Long

    @Query("SELECT * FROM exercise_results")
    suspend fun getAll(): List<ExerciseResult>
}
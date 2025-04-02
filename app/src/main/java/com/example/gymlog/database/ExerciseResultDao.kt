package com.example.gymlog.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseResultDao {
    @Insert
    suspend fun insert(result: ExerciseResult): Long

    // Змініть на suspend, оскільки ви використовуєте runBlocking
    @Query("SELECT * FROM exercise_results ORDER BY timestamp DESC")
    suspend fun getAllResults(): List<ExerciseResult>

    @Update
    suspend fun update(result: ExerciseResult)

    @Delete
    suspend fun delete(result: ExerciseResult)

    @Query("SELECT * FROM exercise_results ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getResultsPaginated(limit: Int, offset: Int): List<ExerciseResult>

    @Query("SELECT * FROM exercise_results WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    suspend fun getResultsByExercise(exerciseId: Long): List<ExerciseResult>
}
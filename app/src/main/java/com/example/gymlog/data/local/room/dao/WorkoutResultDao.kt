package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymlog.data.local.room.entities.workout.WorkoutResultEntity


/**
 * Інтерфейс DAO WorkoutResultDao
 */
@Dao
interface WorkoutResultDao {
    @Insert
    suspend fun insert(result: WorkoutResultEntity): Long

    @Query("SELECT * FROM workout_result WHERE workoutExerciseId = :exerciseId ORDER BY position")
    suspend fun getByExerciseId(exerciseId: Long): List<WorkoutResultEntity>
}
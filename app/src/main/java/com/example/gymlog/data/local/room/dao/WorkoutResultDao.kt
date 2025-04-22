package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.workout.WorkoutResultEntity


/**
 * Інтерфейс DAO WorkoutResultDao
 */
@Dao
interface WorkoutResultDao {
    @Insert
    suspend fun insert(workoutResultEntity: WorkoutResultEntity): Long

    @Update
    suspend fun update(workoutResultEntity: WorkoutResultEntity): Int

    @Delete
    suspend fun delete(workoutResultEntity: WorkoutResultEntity): Int

    // Отримання вправ для конкретного тренування за ID тренування
    @Query("SELECT * FROM workout_result WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun getWorkoutResultEntityByWorkExerciseId(workoutExerciseId: Long): List<WorkoutResultEntity>

}
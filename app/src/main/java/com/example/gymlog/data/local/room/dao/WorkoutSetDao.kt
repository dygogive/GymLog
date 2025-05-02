package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymlog.data.local.room.entities.workout.WorkoutSetEntity

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(set: WorkoutSetEntity): Long

    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workoutId ORDER BY position")
    suspend fun getByWorkoutId(workoutId: Long): List<WorkoutSetEntity>
}

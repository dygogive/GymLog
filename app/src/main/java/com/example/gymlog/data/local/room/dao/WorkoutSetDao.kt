package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

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

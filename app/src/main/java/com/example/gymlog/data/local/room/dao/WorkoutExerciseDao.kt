package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymlog.data.local.room.entities.workout.WorkoutExerciseEntity


/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(exercise: WorkoutExerciseEntity): Long

    @Query("SELECT * FROM WorkoutExercises WHERE workout_set_id = :setId ORDER BY position")
    suspend fun getBySetId(setId: Long): List<WorkoutExerciseEntity>
}

package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow


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

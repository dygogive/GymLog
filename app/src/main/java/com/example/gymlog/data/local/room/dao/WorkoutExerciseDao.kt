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
    suspend fun insert(workoutExerciseEntity: WorkoutExerciseEntity): Long

    @Update
    suspend fun update(workoutExerciseEntity: WorkoutExerciseEntity): Int

    @Delete
    suspend fun delete(workoutExerciseEntity: WorkoutExerciseEntity): Int

    // Отримання вправ для конкретного тренування за ID тренування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutExerciseEntity>

    // Аналогічний запит, але з Flow для реактивного програмування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    fun getWorkExerciseByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutExerciseEntity>>
}

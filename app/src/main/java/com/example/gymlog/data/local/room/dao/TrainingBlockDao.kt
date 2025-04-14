package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import kotlinx.coroutines.flow.Flow


/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface TrainingBlockDao {
    @Insert
    suspend fun insert(trainingBlockEntity: TrainingBlockEntity): Long

    @Update
    suspend fun update(trainingBlockEntity: TrainingBlockEntity): Int

    @Delete
    suspend fun delete(trainingBlockEntity: TrainingBlockEntity): Int

    // запит з Flow для реактивного програмування
    @Query("SELECT * FROM TrainingBlock WHERE gym_day_id = :gym_day_id")
    fun getTrainingBlockByGymDayIDFlow(gym_day_id: Long): Flow<List<TrainingBlockEntity>>

}
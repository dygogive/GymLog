package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entities.plan.TrainingBlockEntity
import kotlinx.coroutines.flow.Flow


/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface TrainingBlockDao {

    @Insert
    suspend fun insert(trainingBlockEntity: TrainingBlockEntity): Long



    //Отримати TrainingBlockEntity по колонці gym_day_id в порядку зростання позиції
    @Query("SELECT * FROM TrainingBlock " +
            "WHERE gym_day_id = :gymDayId " +
            "ORDER BY position ASC")
    suspend fun getBlocksByGymDayId(gymDayId: Long?): List<TrainingBlockEntity>
}
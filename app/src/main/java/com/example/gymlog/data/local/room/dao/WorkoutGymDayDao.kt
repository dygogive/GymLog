package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entities.workout.WorkoutGymDayEntity

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutGymDay (тренувальні дні).
 * Містить методи для CRUD операцій (Create, Read, Update, Delete).
 */
@Dao
interface WorkoutGymDayDao {
    @Insert
    suspend fun insert(workout: WorkoutGymDayEntity): Long

    @Update
    suspend fun update(workout: WorkoutGymDayEntity)

    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    suspend fun getAll(): List<WorkoutGymDayEntity>

    @Query("SELECT * FROM WorkoutGymDay WHERE id = :id")
    suspend fun getById(id: Long): WorkoutGymDayEntity?
}
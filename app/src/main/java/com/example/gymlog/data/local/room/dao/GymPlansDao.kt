package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.plan.PlanCycleEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface GymPlansDao {
    @Insert
    suspend fun insert(planCycleEntity: PlanCycleEntity): Long

    @Update
    suspend fun update(planCycleEntity: PlanCycleEntity): Int

    @Delete
    suspend fun delete(planCycleEntity: PlanCycleEntity): Int

    @Query("SELECT * FROM PlanCycles " +
            "ORDER BY position ASC")
    suspend fun getPlanCycles(): List<PlanCycleEntity>

}

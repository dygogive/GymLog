package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entities.plan.PlanCycleEntity

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface GymPlansDao {
    @Query("SELECT * FROM PlanCycles " +
            "ORDER BY position ASC")
    suspend fun getPlanCycles(): List<PlanCycleEntity>
}

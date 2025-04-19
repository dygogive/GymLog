package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.plan.GymDayEntity

@Dao
interface GymSessionDao {
    @Insert
    suspend fun insert(gymDayEntity: GymDayEntity): Long

    @Update
    suspend fun update(gymDayEntity: GymDayEntity): Int

    @Delete
    suspend fun delete(gymDayEntity: GymDayEntity): Int


    @Query("SELECT * FROM GymDays " +
            "WHERE plan_id = :planId " +
            "ORDER BY position ASC")
    suspend fun getGymDaysEntities(planId: Long): List<GymDayEntity>


}
package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymlog.data.local.room.entities.WorkoutResultEntity


/**
 * Інтерфейс DAO WorkoutResultDao
 */
@Dao
interface WorkoutResultDao {
    @Query("""
        SELECT * FROM workout_result
        WHERE exerciseInBlockId = :exerciseInBlockId
        ORDER BY workoutDateTime DESC, weight DESC, iteration DESC
    """)
    suspend fun getResultsForExercise(
        exerciseInBlockId: Long
    ): List<WorkoutResultEntity>
}
package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymlog.data.local.room.entities.workout.WorkoutResultEntity


/**
 * Інтерфейс DAO WorkoutResultDao
 */
@Dao
interface WorkoutResultDao {
    @Insert
    suspend fun insert(result: WorkoutResultEntity)

    @Query("""
        SELECT * FROM workout_result 
        WHERE exerciseInBlockId = :exerciseInBlockId 
        ORDER BY weight DESC 
        LIMIT :limit
    """)
    suspend fun getLatestResults(exerciseInBlockId: Long, limit: Int): List<WorkoutResultEntity>

    @Query("""
        SELECT * FROM workout_result 
        WHERE exerciseInBlockId = :exerciseInBlockId 
        AND workoutDateTime = :workoutDateTime
    """)
    suspend fun getResultsForDateTime(
        exerciseInBlockId: Long,
        workoutDateTime: String
    ): List<WorkoutResultEntity>
}
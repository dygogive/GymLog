package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
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
        WHERE programUuid = :programUuid
        AND exerciseId = :exerciseId
        AND (:trainingBlockUuid is NULL OR trainingBlockUuid = :trainingBlockUuid)
        ORDER BY workoutDateTime DESC, weight DESC, iteration DESC
    """)
    suspend fun getResultsForExercise(
        programUuid: String,
        exerciseId: Long,
        trainingBlockUuid: String? = null
    ): List<WorkoutResultEntity>


    @Insert
    suspend fun insert(result: WorkoutResultEntity)


    @Query("""
        DELETE FROM workout_result WHERE id = :id
    """)
    suspend fun deleteById(id: Long)



    @Query("""
        UPDATE workout_result
        SET weight = :weight, iteration = :iteration, workTime = :workTime
        WHERE id = :id
    """)
    suspend fun updateResultFields(id: Long?, weight: Int?, iteration: Int?, workTime: Int?)
}
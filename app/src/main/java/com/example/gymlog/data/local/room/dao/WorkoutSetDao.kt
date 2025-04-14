package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSetEntity: WorkoutSetEntity): Long

    @Update
    suspend fun update(workoutSetEntity: WorkoutSetEntity): Int

    @Delete
    suspend fun delete(workoutSetEntity: WorkoutSetEntity): Int

    // Отримання підходів для конкретного тренування за ID тренування
    // Відсортовані за позицією (зростання)
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutSetEntity>

    // Аналогічний запит, але з Flow для спостереження за змінами
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    fun getWorkSetByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutSetEntity>>
}

package com.example.gymlog.data.repository

import android.util.Log
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.insert


@Singleton
class TrainingBlockRepository @Inject constructor(
    private val trainingBlockDao: TrainingBlockDao   // Для роботи з вправами
)  {







    /* ----- Робота з тренувальними блоками (TrainingBlocks) ----- */

    suspend fun insertTrainingBlock(trainingBlockEntity: TrainingBlockEntity): Long {
        return trainingBlockDao.insert(trainingBlockEntity)
    }

    /**
     * Отримує всі вправи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutExercise>> - стрім вправ
     */
    fun getTrainingBlockByGymDay(gymDayID: Long): Flow<List<TrainingBlockEntity>> {
        Log.d("findError", "in getTrainingBlockByGymDay")
        return trainingBlockDao.getTrainingBlockByGymDayIDFlow(gymDayID)
    }
}
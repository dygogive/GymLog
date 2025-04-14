package com.example.gymlog.data.repository

import android.util.Log
import com.example.gymlog.data.local.legacy.PlanManagerDAO
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.entity.TrainingBlockEntity
import com.example.gymlog.data.local.room.mapper.toDomain
import com.example.gymlog.domain.model.exercise.Motion
import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TrainingBlockRepository @Inject constructor(
    private val trainingBlockDao: TrainingBlockDao,         // Для роботи з вправами
    private val legacyDao: PlanManagerDAO,                  //старий Дао
    private val exeInBlockDao: ExerciseInBlockDao,
    private val filterDao: TrainingBlockFilterDao,
)  {







    suspend fun insertTrainingBlock(trainingBlockEntity: TrainingBlockEntity): Long {
        return trainingBlockDao.insert(trainingBlockEntity)
    }


    fun getTrainingBlockByGymDay(gymDayID: Long): Flow<List<TrainingBlockEntity>> {
        Log.d("findError", "in getTrainingBlockByGymDay")
        return trainingBlockDao.getTrainingBlockByGymDayIDFlow(gymDayID)
    }



    suspend fun getTrainingBlocksByDayId(gymDayId: Long): List<TrainingBlock> {
        val blocks = trainingBlockDao.getBlocksByGymDayId(gymDayId).map { it.toDomain() }.toMutableList()

        for (block in blocks) {
            //
            val filters = filterDao.getAllFiltersForBlock(block.id)
//            val exercises = exeInBlockDao.get
            //
            block.motions           = filters.motions
            block.equipmentList     = filters.equipment
            block.muscleGroupList   = filters.muscleGroups
            //
            //

        }

    }



}
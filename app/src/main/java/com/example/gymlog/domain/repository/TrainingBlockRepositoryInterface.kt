package com.example.gymlog.domain.repository

import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity
import com.example.gymlog.domain.model.plan.TrainingBlock
import kotlinx.coroutines.flow.Flow

interface TrainingBlockRepositoryInterface {
    suspend fun insertTrainingBlock(trainingBlockEntity: TrainingBlock): Long
    fun getTrainingBlockByGymDay(gymDayID: Long): Flow<List<TrainingBlock>>
    suspend fun getTrainingBlocksByDayId(gymDayId: Long): List<TrainingBlock>
}
package com.example.gymlog.data.repository.training_block_repository

import com.example.gymlog.core.utils.parseEnumList
import com.example.gymlog.core.utils.parseEnumOrNull
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.legacy.plan.TrainingBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingBlockRepository @Inject constructor(
    private val trainingBlockDao: TrainingBlockDao,         // Для роботи з вправами
    private val exeInBlockDao: ExerciseInBlockDao,
    private val filterDao: TrainingBlockFilterDao,
    ){

    suspend fun insertTrainingBlock(trainingBlock: TrainingBlock): Long {
        return trainingBlockDao.insert(trainingBlock.toEntity())
    }


    suspend fun getTrainingBlocksByDayId(gymDayId: Long): List<TrainingBlock> {
        val blocks = trainingBlockDao.getBlocksByGymDayId(gymDayId).map { it.toDomain() }.toMutableList()

        for (block in blocks) {
            //
            val filters = filterDao.getAllFiltersForBlock(block.id)
            val exercises = exeInBlockDao.getExercisesForBlock(block.id).map { it.toDomain() }.toMutableList()
            //
            block.motions = filters.motions.mapNotNull { parseEnumOrNull<Motion>(it) }
            block.equipmentList = filters.equipment.mapNotNull { parseEnumOrNull<Equipment>(it) }
            block.muscleGroupList = filters.muscleGroups.mapNotNull { parseEnumOrNull<MuscleGroup>(it) }

            //
            //
            block.exercises         = exercises
        }
        return blocks
    }
}
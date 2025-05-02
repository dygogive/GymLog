package com.example.gymlog.data.repository

import com.example.gymlog.core.utils.EnumMapper
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.mappers.toDomainNew
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew
import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitnessProgramNewRepository @Inject constructor(
    private val gymPlansDao: GymPlansDao,
    private val gymSessionDao: GymSessionDao,
    private val trainingBlockDao: TrainingBlockDao,
    private val filterDao: TrainingBlockFilterDao,
    private val exerciseInBlockDao: ExerciseInBlockDao
): FitnessProgramNewRepositoryInterface {

    override suspend fun getAllFitnessProgramsNew(): List<FitnessProgramNew> {
        return gymPlansDao.getPlanCycles().map { planEntity ->
            val gymDays = gymSessionDao.getGymDaysEntities(planEntity.id).map { gymDayEntity ->
                val trainingBlocks = trainingBlockDao.getBlocksByGymDayId(gymDayEntity.id).map { blockEntity ->
                    val filters = filterDao.getAllFiltersForBlock(blockEntity.id)
                    val exercises = exerciseInBlockDao.getExercisesForBlock(blockEntity.id)
                        .map { it.toDomainNew() }

                    blockEntity.toDomainNew(
                        exercises = exercises,
                        motions = filters.motions.map { motionEntity ->
                            EnumMapper.fromString(motionEntity.name, MotionNew.PRESS_BY_LEGS)
                        },
                        muscleGroup = filters.muscleGroups.map { muscleGroupEntity ->
                            EnumMapper.fromString(muscleGroupEntity.name, MuscleGroupNew.CHEST)
                        },
                        equipment = filters.equipment.map { equipmentEntity ->
                            EnumMapper.fromString(equipmentEntity.name, EquipmentNew.BARBELL)
                        }
                    )
                }

                gymDayEntity.toDomainNew(blocks = trainingBlocks)
            }

            planEntity.toDomainNew(gymDays = gymDays)
        }
    }
}
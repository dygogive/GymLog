package com.example.gymlog.data.repository.gym_plan

import com.example.gymlog.core.utils.EnumMapper
import com.example.gymlog.data.local.room.dao.*
import com.example.gymlog.data.local.room.mapper.toDomainNew
import com.example.gymlog.data.local.room.mapper.toDomainNew
import com.example.gymlog.domain.model.plannew.FitnessProgramNew
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
                            EnumMapper.fromString(motionEntity.name, com.example.gymlog.domain.model.attributenew.MotionNew.PRESS_BY_LEGS)
                        },
                        muscleGroup = filters.muscleGroups.map { muscleGroupEntity ->
                            EnumMapper.fromString(muscleGroupEntity.name, com.example.gymlog.domain.model.attributenew.MuscleGroupNew.CHEST)
                        },
                        equipment = filters.equipment.map { equipmentEntity ->
                            EnumMapper.fromString(equipmentEntity.name, com.example.gymlog.domain.model.attributenew.EquipmentNew.BARBELL)
                        }
                    )
                }

                gymDayEntity.toDomainNew(blocks = trainingBlocks)
            }

            planEntity.toDomainNew(gymDays = gymDays)
        }
    }
}
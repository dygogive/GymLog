package com.example.gymlog.data.repository.gym_plan


import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.mapper.toDomainNew
import com.example.gymlog.domain.model.plannew.FitnessProgramNew
import com.example.gymlog.domain.model.plannew.GymDayNew
import com.example.gymlog.domain.model.plannew.TrainingBlockNew
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitnessProgramNewRepository @Inject constructor(
    private val gymPlansDao: GymPlansDao,
    private val gymSessionDao: GymSessionDao,
    private val trainingBlockDao: TrainingBlockDao,
    private val filterDao: TrainingBlockFilterDao,
    private val exerciseInBlockDao: ExerciseInBlockDao
) {

    suspend fun getAllFitnessProgramsNew(): List<FitnessProgramNew> {
        val plans = gymPlansDao.getPlanCycles()

        return plans.map { planEntity ->
            val gymDays = gymSessionDao.getGymDaysEntities(planEntity.id).map { gymDayEntity ->
                val trainingBlocks = trainingBlockDao.getBlocksByGymDayId(gymDayEntity.id).map { blockEntity ->
                    val filters = filterDao.getAllFiltersForBlock(blockEntity.id)
                    val exercises = exerciseInBlockDao.getExercisesForBlock(blockEntity.id)

                    TrainingBlockNew(
                        name = blockEntity.name,
                        description = blockEntity.description ?: "",
                        position = blockEntity.position,
                        exercises = exercises.map { it.toDomainNew() },
                        motions = filters.motions.map { motionEntity ->
                            motionEntity.name.let { motionName ->
                                com.example.gymlog.domain.model.attributenew.MotionNew.valueOf(motionName)
                            }
                        },
                        muscleGroups = filters.muscleGroups.map { muscleGroupEntity ->
                            muscleGroupEntity.name.let { muscleGroupName ->
                                com.example.gymlog.domain.model.attributenew.MuscleGroupNew.valueOf(muscleGroupName)
                            }
                        },
                        equipment = filters.equipment.map { equipmentEntity ->
                            equipmentEntity.name.let { equipmentName ->
                                com.example.gymlog.domain.model.attributenew.EquipmentNew.valueOf(equipmentName)
                            }
                        }
                    )
                }

                GymDayNew(
                    name = gymDayEntity.day_name,
                    description = gymDayEntity.description ?: "",
                    position = gymDayEntity.position,
                    trainingBlocks = trainingBlocks
                )
            }

            FitnessProgramNew(
                name = planEntity.name,
                description = planEntity.description,
                position = planEntity.position,
                creationDate = planEntity.creation_date,
                gymDays = gymDays
            )
        }
    }
}
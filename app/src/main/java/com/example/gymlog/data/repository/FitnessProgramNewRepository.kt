package com.example.gymlog.data.repository

import com.example.gymlog.core.utils.EnumMapper
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.mappers.BlockFiltersMapper.toDomain
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toDomainNew
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew
import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitnessProgramNewRepository @Inject constructor(
    private val gymPlansDao: GymPlansDao,
    private val gymSessionDao: GymSessionDao,
    private val trainingBlockDao: TrainingBlockDao,
    private val filterDao: TrainingBlockFilterDao,
    private val exerciseInBlockDao: ExerciseInBlockDao,
    private val workoutResultDao: WorkoutResultDao,
): FitnessProgramNewRepositoryInterface {

    //get all fitness programs
    override suspend fun getAllFitnessPrograms(): List<FitnessProgramNew> {
        return gymPlansDao.getPlanCycles().map {it.toDomainNew()}.toMutableList()
    }

    //get all GymDats by program ID
    override suspend fun getAllGymDays(idProgram: Long): List<GymDayNew> {
        return gymSessionDao.getGymDaysEntities(planId = idProgram).map {it.toDomainNew()}.toMutableList()
    }


    override suspend fun getSelectedGymDayNew(idGymDay: Long): GymDayNew {
        val gymDayEntity = gymSessionDao.getGymDayById(idGymDay) ?: throw GymDayNotFoundException()

        val trainingBlocks = trainingBlockDao.getBlocksByGymDayId(idGymDay).map { blockEntity ->
            val filters = filterDao.getAllFiltersForBlock(blockEntity.id).toDomain()
            val exercises = exerciseInBlockDao.getExercisesForBlock(blockEntity.id)
                .map { it.toDomainNew() }

            blockEntity.toDomainNew(
                exercises = exercises,
                motions = filters.motions,
                muscleGroup = filters.muscleGroups,
                equipment = filters.equipment
            )
        }

        return gymDayEntity.toDomainNew(blocks = trainingBlocks)
    }


    override suspend fun getWorkoutResultsForExercise(exerciseInBlockId: Long): List<WorkoutResult> {
        return workoutResultDao.getResultsForExercise(exerciseInBlockId)
            .map { it.toDomain() }
    }

}
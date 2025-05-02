package com.example.gymlog.data.repository

import com.example.gymlog.data.local.room.dao.WorkoutExerciseDao
import com.example.gymlog.data.local.room.dao.WorkoutGymDayDao
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.dao.WorkoutSetDao
import com.example.gymlog.data.local.room.entities.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entities.workout.WorkoutResultEntity
import com.example.gymlog.data.local.room.entities.workout.WorkoutSetEntity
import com.example.gymlog.data.local.room.mappers.toDomain
import com.example.gymlog.data.local.room.mappers.toEntity
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторій для роботи з тренувальними даними.
 * Виступає як проміжний шар між ViewModel та DAO.
 *
 * @Singleton - анотація Dagger Hilt, що гарантує єдиний екземпляр класу в додатку
 * @Inject constructor - інжекція залежностей через конструктор
 */
@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutGymDayDao: WorkoutGymDayDao,
    private val workoutSetDao: WorkoutSetDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutResultDao: WorkoutResultDao,
) : WorkoutRepositoryInterface {

    override suspend fun insertWorkoutGymDay(workoutGymDay: WorkoutGymDay): Long {
        // Insert the main workout day
        val workoutId = workoutGymDayDao.insert(workoutGymDay.toEntity())

        // Insert all sets, exercises and results
        workoutGymDay.workoutSets.forEach { set ->
            val setId = workoutSetDao.insert(set.toEntity(workoutId))

            set.workoutExercises.forEach { exercise ->
                val exerciseId = workoutExerciseDao.insert(exercise.toEntity(setId))

                exercise.results.forEach { result ->
                    workoutResultDao.insert(result.toEntity(exerciseId))
                }
            }
        }

        return workoutId
    }



    override suspend fun getWorkoutGymDays(): List<WorkoutGymDay> {
        return workoutGymDayDao.getAll().map { workoutEntity ->
            val sets = workoutSetDao.getByWorkoutId(workoutEntity.id!!).map { setEntity ->
                val exercises = workoutExerciseDao.getBySetId(setEntity.id!!).map { exerciseEntity ->
                    val results = workoutResultDao.getByExerciseId(exerciseEntity.id!!).map { it.toDomain() }
                    exerciseEntity.toDomain(results)
                }
                setEntity.toDomain(exercises)
            }
            workoutEntity.toDomain(sets)
        }
    }

    // Add these methods to the DAOs if they don't exist
    private suspend fun WorkoutSetDao.delete(entity: WorkoutSetEntity) {
        // Implementation depends on your DAO
    }

    private suspend fun WorkoutExerciseDao.update(entity: WorkoutExerciseEntity) {
        // Implementation depends on your DAO
    }

    private suspend fun WorkoutExerciseDao.delete(entity: WorkoutExerciseEntity) {
        // Implementation depends on your DAO
    }

    private suspend fun WorkoutResultDao.update(entity: WorkoutResultEntity) {
        // Implementation depends on your DAO
    }

    private suspend fun WorkoutResultDao.delete(entity: WorkoutResultEntity) {
        // Implementation depends on your DAO
    }
}
package com.example.gymlog.data.repository.workout

import com.example.gymlog.data.local.room.dao.WorkoutExerciseDao
import com.example.gymlog.data.local.room.dao.WorkoutGymDayDao
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.dao.WorkoutSetDao
import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import com.example.gymlog.data.local.room.mapper.toDomain
import com.example.gymlog.data.local.room.mapper.toEntity
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.model.workout.WorkoutSet
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    // Отримуємо DAO через dependency injection
    private val workGymDayDao: WorkoutGymDayDao,     // Для роботи з тренувальними днями
    private val workSetDao: WorkoutSetDao,           // Для роботи з підходами
    private val workExerciseDao: WorkoutExerciseDao,   // Для роботи з вправами
    private val workResultDao: WorkoutResultDao,   // Для роботи з вправами
) : WorkoutRepositoryInterface {

    /* ----- Робота з тренувальними днями (WorkoutGymDay) ----- */

    /**
     * Додає новий тренувальний день до бази даних
     *
     * @param day Об'єкт WorkoutGymDay для збереження
     * @return ID нового запису (Long)
     */
    override suspend fun insertWorkGymDay(day: WorkoutGymDay): Long {
        return workGymDayDao.insert(day.toEntity())
    }

    /**
     * Отримує всі тренувальні дні у вигляді Flow
     * (для реактивного оновлення UI при змінах даних)
     *
     * @return Flow<List<WorkoutGymDay>> - стрім даних
     */
    override fun getAllWorkGymDays(): Flow<List<WorkoutGymDay>> {
        return workGymDayDao.getAllFlow().map { entityList ->
            entityList.map { it.toDomain() }
        }
    }

    override suspend fun getLastThreeFirstResults(exerciseId: Long): List<WorkoutExercise> {
        return workExerciseDao.getLastThreeFirstResults(exerciseId).map { it.toDomain() }.toMutableList()
    }

    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        TODO("Not yet implemented")
    }


    /* ----- Робота з підходами (WorkoutSet) ----- */

    /**
     * Додає новий підхід до бази даних
     *
     * @param set Об'єкт WorkoutSet для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertSet(set: WorkoutSet): Long {
        return workSetDao.insert(set.toEntity())
    }

    /**
     * Отримує всі підходи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutSet>> - стрім підходів
     */
    fun getSetsForDay(dayId: Long): Flow<List<WorkoutSet>> {
        return workSetDao.getWorkSetByWorkDayIDFlow(dayId).map { workSet ->
            workSet.map { it.toDomain() }
        }
    }









    /* ----- Робота з вправами (WorkoutExercise) ----- */

    /**
     * Додає нову вправу до бази даних
     *
     * @param ex Об'єкт WorkoutExercise для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertExercise(ex: WorkoutExercise): Long {
        return workExerciseDao.insert(ex.toEntity())
    }

    /**
     * Отримує всі вправи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutExercise>> - стрім вправ
     */

    fun getExercisesForDay(dayId: Long): Flow<List<WorkoutExercise>> {
        return workExerciseDao.getWorkExerciseByWorkDayIDFlow(dayId).map { workExercise ->
            workExercise.map { it.toDomain() }
        }
    }








}
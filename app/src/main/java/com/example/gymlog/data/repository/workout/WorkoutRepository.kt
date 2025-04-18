package com.example.gymlog.data.repository.workout

import com.example.gymlog.data.local.room.dao.WorkoutExerciseDao
import com.example.gymlog.data.local.room.dao.WorkoutGymDayDao
import com.example.gymlog.data.local.room.dao.WorkoutSetDao
import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import com.example.gymlog.domain.repository.WorkoutRepositoryInterface
import kotlinx.coroutines.flow.Flow
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
) : WorkoutRepositoryInterface {

    /* ----- Робота з тренувальними днями (WorkoutGymDay) ----- */

    /**
     * Додає новий тренувальний день до бази даних
     *
     * @param day Об'єкт WorkoutGymDay для збереження
     * @return ID нового запису (Long)
     */
    override suspend fun insertWorkGymDay(day: WorkoutGymDayEntity): Long {
        return workGymDayDao.insert(day)
    }

    /**
     * Отримує всі тренувальні дні у вигляді Flow
     * (для реактивного оновлення UI при змінах даних)
     *
     * @return Flow<List<WorkoutGymDay>> - стрім даних
     */
    override fun getAllWorkGymDays(): Flow<List<WorkoutGymDayEntity>> {
        return workGymDayDao.getAllFlow()
    }








    /* ----- Робота з підходами (WorkoutSet) ----- */

    /**
     * Додає новий підхід до бази даних
     *
     * @param set Об'єкт WorkoutSet для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertSet(set: WorkoutSetEntity): Long {
        return workSetDao.insert(set)
    }

    /**
     * Отримує всі підходи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutSet>> - стрім підходів
     */
    fun getSetsForDay(dayId: Long): Flow<List<WorkoutSetEntity>> {
        return workSetDao.getWorkSetByWorkDayIDFlow(dayId)
    }









    /* ----- Робота з вправами (WorkoutExercise) ----- */

    /**
     * Додає нову вправу до бази даних
     *
     * @param ex Об'єкт WorkoutExercise для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertExercise(ex: WorkoutExerciseEntity): Long {
        return workExerciseDao.insert(ex)
    }

    /**
     * Отримує всі вправи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutExercise>> - стрім вправ
     */
    fun getExercisesForDay(dayId: Long): Flow<List<WorkoutExerciseEntity>> {
        return workExerciseDao.getWorkExerciseByWorkDayIDFlow(dayId)
    }








}
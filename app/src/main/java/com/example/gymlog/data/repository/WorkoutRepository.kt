package com.example.gymlog.data.repository


import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.entities.workout.WorkoutResultEntity
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
    private val workoutResultDao: WorkoutResultDao,
) : WorkoutRepositoryInterface {



    private suspend fun WorkoutResultDao.update(entity: WorkoutResultEntity) {
        // Implementation depends on your DAO
    }

    private suspend fun WorkoutResultDao.delete(entity: WorkoutResultEntity) {
        // Implementation depends on your DAO
    }
}
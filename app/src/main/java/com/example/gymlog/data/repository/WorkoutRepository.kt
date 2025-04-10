package com.example.gymlog.data.repository

import com.example.gymlog.database.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WorkoutRepository @Inject constructor(
    private val workGymDayDao: WorkoutGymDayDao,
    private val workSetDao: WorkoutSetDao,
    private val workExerciseDao: WorkoutExerciseDao
){
    /* ----- WorkoutGymDay ------*/
    suspend fun insertGymDay(day: WorkoutGymDay): Long {
        return workGymDayDao.insert(day)
    }
    fun getAllGymDays(): Flow<List<WorkoutGymDay>> {
        return workGymDayDao.getAllFlow()
    }

    /* ----- WorkoutSet ----- */
    suspend fun insertSet(set: WorkoutSet): Long {
        return workSetDao.insert(set)
    }
    fun getSetsForDay(dayId: Long): Flow<List<WorkoutSet>> {
        return workSetDao.getWorkSetByWorkDayIDFlow(dayId)
    }

    /* ----- WorkoutExercise ----- */
    suspend fun insertExercise(ex: WorkoutExercise): Long {
        return workExerciseDao.insert(ex)
    }
    fun getExercisesForDay(dayId: Long): Flow<List<WorkoutExercise>> {
        return workExerciseDao.getWorkExerciseByWorkDayIDFlow(dayId)
    }


}
package com.example.gymlog.database.room

import androidx.room.*
import com.example.gymlog.database.DBHelper
import kotlinx.coroutines.flow.Flow


@Database(
    entities = [
        WorkoutGymDay::class,
        WorkoutSet::class,
        WorkoutExercise::class,
        Exercise::class,
        PlanCycle::class,
        GymDay::class,
        TrainingBlock::class,
        TrainingBlockMotion::class,
        TrainingBlockMuscleGroup::class,
        TrainingBlockEquipment::class,
        TrainingBlockExercise::class
    ],
    version = DBHelper.VERSION
)
abstract class WorkoutDatabase: RoomDatabase() {
    abstract fun workoutGymDayDao(): WorkoutGymDayDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
}

@Dao
interface WorkoutGymDayDao {
    @Insert
    suspend fun insert(workoutGymDay: WorkoutGymDay): Long

    @Update
    suspend fun update(workoutGymDay: WorkoutGymDay): Int

    @Delete
    suspend fun delete(workoutGymDay: WorkoutGymDay): Int

    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    suspend fun getAll(): List<WorkoutGymDay>

    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    fun getAllFlow(): Flow<List<WorkoutGymDay>>
}


@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSet): Long

    @Update
    suspend fun update(workoutSet: WorkoutSet): Int

    @Delete
    suspend fun delete(workoutSet: WorkoutSet ): Int

    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutSet>

    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    fun getWorkSetByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutSet>>
}


@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun update(workoutExercise: WorkoutExercise): Int

    @Delete
    suspend fun delete(workoutExercise: WorkoutExercise): Int

    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutExercise>

    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    fun getWorkExerciseByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutExercise>>
}
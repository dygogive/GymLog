package com.example.gymlog.database.room

import androidx.room.*
import com.example.gymlog.database.DBHelper


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
    suspend fun update(workoutGymDay: WorkoutGymDay): Long

    @Delete
    suspend fun delete(workoutGymDay: WorkoutGymDay): Long

    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    suspend fun getAll(): List<WorkoutGymDay>
}


@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSet): Long

    @Update
    suspend fun update(workoutSet: WorkoutSet): Long

    @Delete
    suspend fun delete(workoutSet: WorkoutSet ): Long

    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutSet>
}


@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun update(workoutExercise: WorkoutExercise): Long

    @Delete
    suspend fun delete(workoutExercise: WorkoutExercise): Long

    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutExercise>
}
package com.example.gymlog.database.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDb(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, WorkoutDatabase::class.java, "GymLog.db").build()

    @Provides
    fun provideWorkoutGymDayDao(db: WorkoutDatabase): WorkoutGymDayDao {
        return db.workoutGymDayDao()
    }

    @Provides
    fun provideWorkoutSetDao(db: WorkoutDatabase): WorkoutSetDao {
        return db.workoutSetDao()
    }

    @Provides
    fun provideWorkoutExerciseDao(db: WorkoutDatabase): WorkoutExerciseDao {
        return db.workoutExerciseDao()
    }

}
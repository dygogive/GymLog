package com.example.gymlog.database.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.migration.DisableInstallInCheck

@Module
@DisableInstallInCheck
object DatabaseModule {

    @Provides
    fun provideDb(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, WorkoutDatabase::class.java, "GymLog.db").build()

    @Provides
    fun provideWorkoutGymDayDao(db: WorkoutDatabase)    = db.workoutGymDayDao()

    @Provides
    fun provideWorkoutSetDao(db: WorkoutDatabase)       = db.workoutSetDao()

    @Provides
    fun provideWorkoutExerciseDao(db: WorkoutDatabase)  = db.workoutExerciseDao()

}
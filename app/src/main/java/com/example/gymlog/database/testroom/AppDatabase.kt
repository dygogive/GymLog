package com.example.gymlog.database.testroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities =
    [
        ExerciseResult::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getExerciseDao(): ExerciseResultDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app-database.db"
            ).build().also { instance = it }
        }
    }
}
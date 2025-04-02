package com.example.gymlog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseResult::class], version = 1, exportSchema = false)
abstract class GymLogRoomDatabase : RoomDatabase() {

    //абстрактний метод, що реалузується в наслідуваному класі
    //як видног, він повертає реалузований об'єкт ExerciseResultDao
    abstract fun exerciseResultDao(): ExerciseResultDao

    // тут створюються статичні змінні й методи класу GymLogRoomDatabase
    companion object {

        @Volatile  // екземпляр INSTANCE оригінальний, а не кешований
        private var INSTANCE: GymLogRoomDatabase? = null;

        //видає екземпляр або створює новий
        fun getInstance(context: Context) : GymLogRoomDatabase {
            //якщо INSTANCE == null то створює екземпляр
            //synchronized забезпечує створення лише одним потоком.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymLogRoomDatabase::class.java, //потрібний тип Class(Java), а не KClass(Kotlin),
                    "gym_log_room.db"  // назва бази?
                ).fallbackToDestructiveMigration()
                    .build();
                INSTANCE = instance //передає ссилку на instance
                instance // повертає instance
            }
        }
    }


}
package com.example.gymlog.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Database(entities = [ExerciseResult::class], version = 1, exportSchema = false)
abstract class GymLogRoomDatabase : RoomDatabase() {

    //абстрактний метод, що реалузується в наслідуваному класі
    //як видног, він повертає реалузований об'єкт ExerciseResultDao
    abstract fun exerciseResultDao(): ExerciseResultDao

    // тут створюються статичні змінні й методи класу GymLogRoomDatabase
    companion object {

        private const val DATABASE_NAME = "gym_log_room.db"

        @Volatile  // екземпляр INSTANCE оригінальний, а не кешований
        private var INSTANCE: GymLogRoomDatabase? = null;

        // Приклад міграції (для майбутніх оновлень схеми БД)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Тут виконуємо зміни схеми при оновленні з версії 1 до 2
                database.execSQL("ALTER TABLE exercise_results ADD COLUMN is_completed INTEGER NOT NULL DEFAULT 0")
            }
        }



        //видає екземпляр або створює новий
        fun getInstance(context: Context): GymLogRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }


        private fun buildDatabase(context: Context): GymLogRoomDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GymLogRoomDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d("Database", "Database created")
                        // Можна виконати початкове наповнення БД
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.d("Database", "Database opened")
                    }
                })
                .addMigrations(MIGRATION_1_2) // Додаємо всі необхідні міграції
                .setQueryCallback({ sqlQuery, bindArgs ->
                    Log.d("ROOM_QUERY", "SQL: $sqlQuery | Args: ${bindArgs.joinToString()}")
                }, Executors.newSingleThreadExecutor())
                .build()
        }


    }


}
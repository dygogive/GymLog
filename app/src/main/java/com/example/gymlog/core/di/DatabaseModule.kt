package com.example.gymlog.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymlog.data.local.room.WorkoutDatabase
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Dagger Hilt модуль для налаштування залежностей, пов'язаних з базою даних.
 * Використовується для dependency injection у всьому додатку.
 *
 * @Module - анотація Dagger, що позначає клас як модуль залежностей
 * @InstallIn - вказує, де будуть доступні ці залежності (SingletonComponent - глобальний рівень)
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    val MIGRATION_21_22 = object : Migration(21, 22) {  // Нова міграція
        override fun migrate(database: SupportSQLiteDatabase) {
            // Логіка міграції (якщо потрібна)
        }
    }

    val MIGRATION_22_23 = object : Migration(22, 23) {  // Нова міграція
        override fun migrate(database: SupportSQLiteDatabase) {
            // Логіка міграції (якщо потрібна)
        }
    }

    /**
     * Провайдер для створення екземпляра бази даних Room.
     *
     * @param ctx Контекст додатка (автоматично інжектується через @ApplicationContext)
     * @return Екземпляр WorkoutDatabase
     *
     * Room.databaseBuilder створює базу даних з назвою "GymLog.db"
     */
    @Provides
    fun provideDb(@ApplicationContext ctx: Context): WorkoutDatabase {
        return Room.databaseBuilder(
            ctx,
            WorkoutDatabase::class.java,
            "GymLog.db"
        )
            .addMigrations(MIGRATION_21_22, MIGRATION_22_23) // Додаємо всі міграції
            .build()
    }

    /**
     * Провайдер для отримання DAO для роботи з результатами.
     *
     * @param db Екземпляр бази даних
     * @return Екземпляр WorkoutResultDao
     */
    @Provides
    fun provideWorkoutResultDao(db: WorkoutDatabase): WorkoutResultDao {
        return db.workoutResultDao() // Отримуємо DAO з бази даних
    }

    /**
     * Провайдер для отримання DAO для роботи з вправами у тренуваннях.
     *
     * @param db Екземпляр бази даних
     * @return Екземпляр WorkoutExerciseDao
     */
    @Provides
    fun provideTrainingBlockDao(db: WorkoutDatabase): TrainingBlockDao {
        return db.trainingBlockDao() // Отримуємо DAO з бази даних
    }

    @Provides
    fun provideExerciseInBlockDao(db: WorkoutDatabase): ExerciseInBlockDao {
        return db.exerciseInBlockDao() // Отримуємо DAO з бази даних
    }

    @Provides
    fun provideTrainingBlockFilterDao(db: WorkoutDatabase): TrainingBlockFilterDao {
        return db.trainingBlockFilterDao() // Отримуємо DAO з бази даних
    }

    @Provides
    fun provideGymPlansDao(db: WorkoutDatabase): GymPlansDao {
        return db.gymPlansDao() // Отримуємо DAO з бази даних
    }

    @Provides
    fun provideGymSessionDao(db: WorkoutDatabase): GymSessionDao {
        return db.gymSessionDao() // Отримуємо DAO з бази даних
    }
}
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
            ctx,                       // Контекст додатка
            WorkoutDatabase::class.java, // Клас бази даних
            "GymLog.db"               // Назва файлу бази даних
        ).addMigrations(MIGRATION_21_22).build()                      // Створення екземпляру бази даних
    }

    // Додайте цей код у ваш файл WorkoutDatabase.kt або окремий файл Migration.kt
    val MIGRATION_21_22 = object : Migration(21, 22) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            // Додаємо колонку uuid до PlanCycles
//            database.execSQL("ALTER TABLE PlanCycles ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
//
//            // Генеруємо UUID для PlanCycles (SQLite-спосіб)
//            database.execSQL("UPDATE PlanCycles SET uuid = hex(randomblob(16))")
//
//            // Додаємо колонку uuid до TrainingBlock
//            database.execSQL("ALTER TABLE TrainingBlock ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
//
//            // Генеруємо UUID для TrainingBlock
//            database.execSQL("UPDATE TrainingBlock SET uuid = hex(randomblob(16))")
//
//            // Створюємо нову таблицю workout_result
//            database.execSQL("""
//            CREATE TABLE IF NOT EXISTS workout_result_new (
//                id INTEGER PRIMARY KEY AUTOINCREMENT,
//                programUuid TEXT NOT NULL,
//                trainingBlockUuid TEXT,
//                exerciseId INTEGER NOT NULL,
//                weight INTEGER,
//                iteration INTEGER,
//                workTime INTEGER,
//                sequenceInGymDay INTEGER NOT NULL,
//                position INTEGER NOT NULL,
//                timeFromStart INTEGER NOT NULL,
//                workoutDateTime TEXT NOT NULL,
//                FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE
//            )
//        """.trimIndent())
//
//            // Переносимо дані зі старої таблиці
//            database.execSQL("""
//            INSERT INTO workout_result_new (
//                id, exerciseId, weight, iteration, workTime, sequenceInGymDay,
//                position, timeFromStart, workoutDateTime, programUuid, trainingBlockUuid
//            )
//            SELECT
//                wr.id, tbe.exerciseId, wr.weight, wr.iteration, wr.workTime,
//                wr.sequenceInGymDay, wr.position, wr.timeFromStart, wr.workoutDateTime,
//                pc.uuid, tb.uuid
//            FROM workout_result wr
//            JOIN TrainingBlockExercises tbe ON wr.exerciseInBlockId = tbe.id
//            JOIN TrainingBlock tb ON tbe.trainingBlockId = tb.id
//            JOIN GymDays gd ON tb.gym_day_id = gd.id
//            JOIN PlanCycles pc ON gd.plan_id = pc.id
//        """.trimIndent())
//
//            // Видаляємо стару таблицю та перейменовуємо нову
//            database.execSQL("DROP TABLE workout_result")
//            database.execSQL("ALTER TABLE workout_result_new RENAME TO workout_result")
//
//            // Створюємо індекси
//            database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_programUuid ON workout_result(programUuid)")
//            database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_trainingBlockUuid ON workout_result(trainingBlockUuid)")
//            database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseId ON workout_result(exerciseId)")
//        }
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


/**
 * Ключові моменти:
 *
 * Dagger Hilt Integration:
 *
 * Модуль використовує Dagger Hilt для dependency injection
 *
 * Всі залежності доступні на рівні всього додатка (SingletonComponent)
 *
 * Архітектура:
 *
 * Один централізований модуль для всіх залежностей бази даних
 *
 * Кожен DAO має окремий провайдер
 *
 * Переваги такого підходу:
 *
 * Легке управління залежностями
 *
 * Просте тестування (можна замінити реальну базу даних на mock)
 *
 * Автоматичне керування життєвим циклом об'єктів
 *
 * Робота з Room:
 *
 * База даних створюється один раз при старті додатка
 *
 * Всі DAO отримуються з цього ж екземпляру бази даних
 *
 * Назва файлу бази даних - "GymLog.db"
 *
 * Сучасні підходи:
 *
 * Використання DI (Dependency Injection) для кращої підтримки та тестування
 *
 * Розділення відповідальностей (окремий модуль для бази даних)
 *
 * Цей модуль є ключовим для роботи з базою даних у всьому додатку, забезпечуючи централізоване управління всіма DAO та єдиним екземпляром бази даних.
 * */
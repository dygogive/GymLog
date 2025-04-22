package com.example.gymlog.core.di

import android.content.Context
import androidx.room.Room
import com.example.gymlog.data.local.room.WorkoutDatabase
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.GymPlansDao
import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.dao.WorkoutExerciseDao
import com.example.gymlog.data.local.room.dao.WorkoutGymDayDao
import com.example.gymlog.data.local.room.dao.WorkoutResultDao
import com.example.gymlog.data.local.room.dao.WorkoutSetDao
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
        ).build()                      // Створення екземпляру бази даних
    }

    /**
     * Провайдер для отримання DAO для роботи з тренувальними днями.
     *
     * @param db Екземпляр бази даних (автоматично інжектується)
     * @return Екземпляр WorkoutGymDayDao
     */
    @Provides
    fun provideWorkoutGymDayDao(db: WorkoutDatabase): WorkoutGymDayDao {
        return db.workoutGymDayDao()  // Отримуємо DAO з бази даних
    }

    /**
     * Провайдер для отримання DAO для роботи з підходами у тренуваннях.
     *
     * @param db Екземпляр бази даних
     * @return Екземпляр WorkoutSetDao
     */
    @Provides
    fun provideWorkoutSetDao(db: WorkoutDatabase): WorkoutSetDao {
        return db.workoutSetDao()     // Отримуємо DAO з бази даних
    }

    /**
     * Провайдер для отримання DAO для роботи з вправами у тренуваннях.
     *
     * @param db Екземпляр бази даних
     * @return Екземпляр WorkoutExerciseDao
     */
    @Provides
    fun provideWorkoutExerciseDao(db: WorkoutDatabase): WorkoutExerciseDao {
        return db.workoutExerciseDao() // Отримуємо DAO з бази даних
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
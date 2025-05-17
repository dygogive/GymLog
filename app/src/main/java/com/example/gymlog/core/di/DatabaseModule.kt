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
     * Міграція з версії 21 на версію 22 бази даних.
     * Ця міграція синхронізує Room з оновленнями, які вже виконані в SQLiteOpenHelper.
     */
    val MIGRATION_21_22 = object : Migration(21, 22) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Тут ми не виконуємо реальну міграцію, оскільки вона вже відбулась у SQLiteOpenHelper,
            // але ми повідомляємо Room про зміни структури

            // Переконуємося, що колонка uuid існує у таблиці PlanCycles
            val planCyclesColumns = getColumns(database, "PlanCycles")
            if (!planCyclesColumns.contains("uuid")) {
                database.execSQL("ALTER TABLE PlanCycles ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
            }

            // Переконуємося, що колонка uuid існує у таблиці TrainingBlock
            val trainingBlockColumns = getColumns(database, "TrainingBlock")
            if (!trainingBlockColumns.contains("uuid")) {
                database.execSQL("ALTER TABLE TrainingBlock ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")
            }

            // Перевіряємо чи існує нова таблиця workout_result з правильною структурою
            val workoutResultExists = tableExists(database, "workout_result")
            if (!workoutResultExists) {
                // Створюємо таблицю заново, якщо вона не існує
                database.execSQL("CREATE TABLE IF NOT EXISTS workout_result (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "programUuid TEXT NOT NULL, " +
                        "trainingBlockUuid TEXT, " +
                        "exerciseId INTEGER NOT NULL, " +
                        "weight INTEGER, " +
                        "iteration INTEGER, " +
                        "workTime INTEGER, " +
                        "sequenceInGymDay INTEGER NOT NULL, " +
                        "position INTEGER NOT NULL, " +
                        "timeFromStart INTEGER NOT NULL, " +
                        "workoutDateTime TEXT NOT NULL, " +
                        "FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE" +
                        ")")

                // Створюємо індекси
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_programUuid ON workout_result(programUuid)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_trainingBlockUuid ON workout_result(trainingBlockUuid)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseId ON workout_result(exerciseId)")
            } else {
                // Перевіряємо структуру таблиці workout_result
                val workoutResultColumns = getColumns(database, "workout_result")
                if (!workoutResultColumns.contains("programUuid") ||
                    !workoutResultColumns.contains("trainingBlockUuid") ||
                    !workoutResultColumns.contains("workoutDateTime")) {

                    // Якщо структура таблиці не відповідає очікуваній, створюємо тимчасову таблицю
                    database.execSQL("CREATE TABLE IF NOT EXISTS workout_result_new (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "programUuid TEXT NOT NULL, " +
                            "trainingBlockUuid TEXT, " +
                            "exerciseId INTEGER NOT NULL, " +
                            "weight INTEGER, " +
                            "iteration INTEGER, " +
                            "workTime INTEGER, " +
                            "sequenceInGymDay INTEGER NOT NULL, " +
                            "position INTEGER NOT NULL, " +
                            "timeFromStart INTEGER NOT NULL, " +
                            "workoutDateTime TEXT NOT NULL, " +
                            "FOREIGN KEY (exerciseId) REFERENCES Exercise(id) ON DELETE CASCADE" +
                            ")")

                    // Переміщуємо дані зі старої таблиці в нову (тільки ті стовпці, які існують)
                    try {
                        database.execSQL("INSERT INTO workout_result_new (" +
                                "id, exerciseId, weight, iteration, workTime, sequenceInGymDay, " +
                                "position, timeFromStart, workoutDateTime, programUuid, trainingBlockUuid) " +
                                "SELECT wr.id, tbe.exerciseId, wr.weight, wr.iteration, wr.workTime, " +
                                "wr.sequenceInGymDay, wr.position, wr.timeFromStart, wr.workoutDateTime, " +
                                "pc.uuid, tb.uuid " +
                                "FROM workout_result wr " +
                                "JOIN TrainingBlockExercises tbe ON wr.exerciseInBlockId = tbe.id " +
                                "JOIN TrainingBlock tb ON tbe.trainingBlockId = tb.id " +
                                "JOIN GymDays gd ON tb.gym_day_id = gd.id " +
                                "JOIN PlanCycles pc ON gd.plan_id = pc.id")
                    } catch (e: Exception) {
                        // Якщо виникає помилка при міграції даних, просто створюємо порожню таблицю
                    }

                    // Видаляємо стару таблицю та перейменовуємо нову
                    database.execSQL("DROP TABLE workout_result")
                    database.execSQL("ALTER TABLE workout_result_new RENAME TO workout_result")

                    // Створюємо індекси
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_programUuid ON workout_result(programUuid)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_trainingBlockUuid ON workout_result(trainingBlockUuid)")
                    database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_result_exerciseId ON workout_result(exerciseId)")
                }
            }
        }

        // Допоміжний метод для отримання списку колонок таблиці
        private fun getColumns(db: SupportSQLiteDatabase, tableName: String): List<String> {
            val columns = mutableListOf<String>()
            db.query("PRAGMA table_info($tableName)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(nameIndex))
                }
            }
            return columns
        }

        // Допоміжний метод для перевірки існування таблиці
        private fun tableExists(db: SupportSQLiteDatabase, tableName: String): Boolean {
            db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'").use { cursor ->
                return cursor.count > 0
            }
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
            ctx,                       // Контекст додатка
            WorkoutDatabase::class.java, // Клас бази даних
            "GymLog.db"               // Назва файлу бази даних
        )
            .addMigrations(MIGRATION_21_22) // Додаємо міграцію
            .build()                      // Створення екземпляру бази даних
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
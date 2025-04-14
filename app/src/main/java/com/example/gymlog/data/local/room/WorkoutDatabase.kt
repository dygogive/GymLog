// Визначаємо пакет, де знаходиться цей файл
package com.example.gymlog.data.local.room

// Імпортуємо необхідні бібліотеки та класи
import androidx.room.*
import com.example.gymlog.data.local.legacy.DBHelper
import kotlinx.coroutines.flow.Flow

/**
 * Абстрактний клас бази даних Room, який визначає структуру бази даних додатка.
 * Вказуємо всі сутності (таблиці), які будуть у базі даних, та версію бази.
 *
 * @Database - анотація Room, що позначає клас як базу даних
 *   entities - список всіх класів-сутностей (таблиць) у базі
 *   version - версія бази даних (береться з DBHelper.VERSION)
 */
@Database(
    entities = [
        WorkoutGymDayEntity::class,        // Таблиця тренувальних днів
        WorkoutSetEntity::class,           // Таблиця підходів у тренуванні
        WorkoutExerciseEntity::class,      // Таблиця вправ у тренуванні
        ExerciseEntity::class,             // Таблиця вправ (загальний каталог)
        PlanCycleEntity::class,            // Таблиця циклів тренувального плану
        GymDayEntity::class,               // Таблиця днів тренувань у спортзалі
        TrainingBlockEntity::class,        // Таблиця тренувальних блоків
        TrainingBlockMotionEntity::class,  // Таблиця рухів у тренувальних блоках
        TrainingBlockMuscleGroupEntity::class, // Таблиця груп м'язів у блоках
        TrainingBlockEquipmentEntity::class,   // Таблиця обладнання для блоків
        TrainingBlockExerciseEntity::class     // Таблиця вправ у тренувальних блоках
    ],
    version = DBHelper.VERSION,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ]
)
abstract class WorkoutDatabase: RoomDatabase() {
    // Оголошуємо методи для отримання DAO (Data Access Object) для кожної таблиці
    abstract fun workoutGymDayDao(): WorkoutGymDayDao    // DAO для тренувальних днів
    abstract fun workoutSetDao(): WorkoutSetDao          // DAO для підходів
    abstract fun workoutExerciseDao(): WorkoutExerciseDao // DAO для вправ
    abstract fun trainingBlockDao(): TrainingBlockDao // DAO для тренув блоків
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutGymDay (тренувальні дні).
 * Містить методи для CRUD операцій (Create, Read, Update, Delete).
 */
@Dao
interface WorkoutGymDayDao {
    @Insert  // Анотація для вставки нового запису
    suspend fun insert(workoutGymDayEntity: WorkoutGymDayEntity): Long  // Повертає ID нового запису

    @Update  // Анотація для оновлення запису
    suspend fun update(workoutGymDayEntity: WorkoutGymDayEntity): Int   // Повертає кількість оновлених рядків

    @Delete  // Анотація для видалення запису
    suspend fun delete(workoutGymDayEntity: WorkoutGymDayEntity): Int   // Повертає кількість видалених рядків

    // Отримання всіх записів, відсортованих за датою (новий до старого)
    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    suspend fun getAll(): List<WorkoutGymDayEntity>  // Звичайний список (для синхронних операцій)

    // Аналогічний запит, але повертає Flow для спостереження за змінами в реальному часі
    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    fun getAllFlow(): Flow<List<WorkoutGymDayEntity>>  // Flow для реактивного програмування
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSetEntity: WorkoutSetEntity): Long

    @Update
    suspend fun update(workoutSetEntity: WorkoutSetEntity): Int

    @Delete
    suspend fun delete(workoutSetEntity: WorkoutSetEntity): Int

    // Отримання підходів для конкретного тренування за ID тренування
    // Відсортовані за позицією (зростання)
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutSetEntity>

    // Аналогічний запит, але з Flow для спостереження за змінами
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    fun getWorkSetByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutSetEntity>>
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExerciseEntity: WorkoutExerciseEntity): Long

    @Update
    suspend fun update(workoutExerciseEntity: WorkoutExerciseEntity): Int

    @Delete
    suspend fun delete(workoutExerciseEntity: WorkoutExerciseEntity): Int

    // Отримання вправ для конкретного тренування за ID тренування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutExerciseEntity>

    // Аналогічний запит, але з Flow для реактивного програмування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    fun getWorkExerciseByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutExerciseEntity>>
}



/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface TrainingBlockDao {
    @Insert
    suspend fun insert(trainingBlockEntity: TrainingBlockEntity): Long

    @Update
    suspend fun update(trainingBlockEntity: TrainingBlockEntity): Int

    @Delete
    suspend fun delete(trainingBlockEntity: TrainingBlockEntity): Int

    // запит з Flow для реактивного програмування
    @Query("SELECT * FROM TrainingBlock WHERE gym_day_id = :gym_day_id")
    fun getTrainingBlockByGymDayIDFlow(gym_day_id: Long): Flow<List<TrainingBlockEntity>>

}


/**
 *Ключові моменти:
 * Це основа для роботи з базою даних за допомогою Room ORM
 * Використовує сучасні підходи:
 * Kotlin Coroutines (suspend функції)
 * Flow для реактивного програмування
 * Кожен DAO містить:
 * Базові CRUD операції
 * Специфічні запити для отримання даних
 * Два варіанти запитів: звичайний (синхронний) і Flow (для спостереження)
 * База даних містить комплексну структуру для тренувального додатку:
 * Вправи, підходи, тренувальні дні
 * Тренувальні плани, цикли, блоки
 * Класифікатори вправ (обладнання, групи м'язів тощо)
 * */
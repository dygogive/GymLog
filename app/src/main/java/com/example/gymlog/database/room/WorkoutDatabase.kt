// Визначаємо пакет, де знаходиться цей файл
package com.example.gymlog.database.room

// Імпортуємо необхідні бібліотеки та класи
import androidx.room.*
import com.example.gymlog.database.DBHelper
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
        WorkoutGymDay::class,        // Таблиця тренувальних днів
        WorkoutSet::class,           // Таблиця підходів у тренуванні
        WorkoutExercise::class,      // Таблиця вправ у тренуванні
        Exercise::class,             // Таблиця вправ (загальний каталог)
        PlanCycle::class,            // Таблиця циклів тренувального плану
        GymDay::class,               // Таблиця днів тренувань у спортзалі
        TrainingBlock::class,        // Таблиця тренувальних блоків
        TrainingBlockMotion::class,  // Таблиця рухів у тренувальних блоках
        TrainingBlockMuscleGroup::class, // Таблиця груп м'язів у блоках
        TrainingBlockEquipment::class,   // Таблиця обладнання для блоків
        TrainingBlockExercise::class     // Таблиця вправ у тренувальних блоках
    ],
    version = DBHelper.VERSION
)
abstract class WorkoutDatabase: RoomDatabase() {
    // Оголошуємо методи для отримання DAO (Data Access Object) для кожної таблиці
    abstract fun workoutGymDayDao(): WorkoutGymDayDao    // DAO для тренувальних днів
    abstract fun workoutSetDao(): WorkoutSetDao          // DAO для підходів
    abstract fun workoutExerciseDao(): WorkoutExerciseDao // DAO для вправ
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutGymDay (тренувальні дні).
 * Містить методи для CRUD операцій (Create, Read, Update, Delete).
 */
@Dao
interface WorkoutGymDayDao {
    @Insert  // Анотація для вставки нового запису
    suspend fun insert(workoutGymDay: WorkoutGymDay): Long  // Повертає ID нового запису

    @Update  // Анотація для оновлення запису
    suspend fun update(workoutGymDay: WorkoutGymDay): Int   // Повертає кількість оновлених рядків

    @Delete  // Анотація для видалення запису
    suspend fun delete(workoutGymDay: WorkoutGymDay): Int   // Повертає кількість видалених рядків

    // Отримання всіх записів, відсортованих за датою (новий до старого)
    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    suspend fun getAll(): List<WorkoutGymDay>  // Звичайний список (для синхронних операцій)

    // Аналогічний запит, але повертає Flow для спостереження за змінами в реальному часі
    @Query("SELECT * FROM WorkoutGymDay ORDER BY datetime DESC")
    fun getAllFlow(): Flow<List<WorkoutGymDay>>  // Flow для реактивного програмування
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutSet (підходи у тренуванні).
 */
@Dao
interface WorkoutSetDao {
    @Insert
    suspend fun insert(workoutSet: WorkoutSet): Long

    @Update
    suspend fun update(workoutSet: WorkoutSet): Int

    @Delete
    suspend fun delete(workoutSet: WorkoutSet): Int

    // Отримання підходів для конкретного тренування за ID тренування
    // Відсортовані за позицією (зростання)
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutSet>

    // Аналогічний запит, але з Flow для спостереження за змінами
    @Query("SELECT * FROM WorkoutSet WHERE workout_id = :workGymDayID ORDER BY position ASC")
    fun getWorkSetByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutSet>>
}

/**
 * Інтерфейс DAO для роботи з таблицею WorkoutExercises (вправи у тренуванні).
 */
@Dao
interface WorkoutExerciseDao {
    @Insert
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun update(workoutExercise: WorkoutExercise): Int

    @Delete
    suspend fun delete(workoutExercise: WorkoutExercise): Int

    // Отримання вправ для конкретного тренування за ID тренування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    suspend fun getWorkSetByWorkDayID(workGymDayID: Long): List<WorkoutExercise>

    // Аналогічний запит, але з Flow для реактивного програмування
    @Query("SELECT * FROM WorkoutExercises WHERE workout_gymday_ID = :workGymDayID")
    fun getWorkExerciseByWorkDayIDFlow(workGymDayID: Long): Flow<List<WorkoutExercise>>
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
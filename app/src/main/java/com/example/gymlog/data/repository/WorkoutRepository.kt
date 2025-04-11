// Визначаємо пакет, де знаходиться цей файл
package com.example.gymlog.data.repository

// Імпортуємо необхідні бібліотеки та класи
import com.example.gymlog.database.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторій для роботи з тренувальними даними.
 * Виступає як проміжний шар між ViewModel та DAO.
 *
 * @Singleton - анотація Dagger Hilt, що гарантує єдиний екземпляр класу в додатку
 * @Inject constructor - інжекція залежностей через конструктор
 */
@Singleton
class WorkoutRepository @Inject constructor(
    // Отримуємо DAO через dependency injection
    private val workGymDayDao: WorkoutGymDayDao,     // Для роботи з тренувальними днями
    private val workSetDao: WorkoutSetDao,           // Для роботи з підходами
    private val workExerciseDao: WorkoutExerciseDao   // Для роботи з вправами
) {
    /* ----- Робота з тренувальними днями (WorkoutGymDay) ----- */

    /**
     * Додає новий тренувальний день до бази даних
     *
     * @param day Об'єкт WorkoutGymDay для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertGymDay(day: WorkoutGymDay): Long {
        return workGymDayDao.insert(day)
    }

    /**
     * Отримує всі тренувальні дні у вигляді Flow
     * (для реактивного оновлення UI при змінах даних)
     *
     * @return Flow<List<WorkoutGymDay>> - стрім даних
     */
    fun getAllGymDays(): Flow<List<WorkoutGymDay>> {
        return workGymDayDao.getAllFlow()
    }

    /* ----- Робота з підходами (WorkoutSet) ----- */

    /**
     * Додає новий підхід до бази даних
     *
     * @param set Об'єкт WorkoutSet для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertSet(set: WorkoutSet): Long {
        return workSetDao.insert(set)
    }

    /**
     * Отримує всі підходи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutSet>> - стрім підходів
     */
    fun getSetsForDay(dayId: Long): Flow<List<WorkoutSet>> {
        return workSetDao.getWorkSetByWorkDayIDFlow(dayId)
    }

    /* ----- Робота з вправами (WorkoutExercise) ----- */

    /**
     * Додає нову вправу до бази даних
     *
     * @param ex Об'єкт WorkoutExercise для збереження
     * @return ID нового запису (Long)
     */
    suspend fun insertExercise(ex: WorkoutExercise): Long {
        return workExerciseDao.insert(ex)
    }

    /**
     * Отримує всі вправи для конкретного тренувального дня
     *
     * @param dayId ID тренувального дня
     * @return Flow<List<WorkoutExercise>> - стрім вправ
     */
    fun getExercisesForDay(dayId: Long): Flow<List<WorkoutExercise>> {
        return workExerciseDao.getWorkExerciseByWorkDayIDFlow(dayId)
    }
}

/**
 * Ключові моменти:
 *
 * Архітектурна роль:
 *
 * Репозиторій є частиною шаблону "Repository Pattern"
 *
 * Виступає проміжним шаром між ViewModel (UI логіка) і DAO (доступ до даних)
 *
 * Інкапсулює всю логіку роботи з даними
 *
 * Dependency Injection:
 *
 * Використовує Dagger Hilt для отримання DAO
 *
 * Позначений як @Singleton для уникнення дублювання екземплярів
 *
 * Функціональність:
 *
 * Надає методи для всіх основних операцій CRUD
 *
 * Для отримання даних використовує Flow для реактивного програмування
 *
 * Для модифікації даних використовує suspend функції
 *
 * Переваги:
 *
 * Централізоване управління логікою доступу до даних
 *
 * Легкість тестування (можна мокувати репозиторій)
 *
 * Відокремлення логіки доступу до даних від UI логіки
 *
 * Сучасні підходи:
 *
 * Використання Kotlin Coroutines (suspend функції)
 *
 * Реактивне програмування через Flow
 *
 * Чітке розділення відповідальностей
 *
 * Цей репозиторій є ключовим елементом архітектури додатка, який забезпечує чистий API для роботи з тренувальними даними та спрощує подальшу підтримку та розширення функціоналу.
 *
 * New chat
 * */
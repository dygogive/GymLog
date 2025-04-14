package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.entity.WorkoutGymDayEntity
import kotlinx.coroutines.flow.Flow

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
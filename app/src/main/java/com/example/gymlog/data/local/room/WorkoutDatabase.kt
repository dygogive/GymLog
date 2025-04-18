// Визначаємо пакет, де знаходиться цей файл
package com.example.gymlog.data.local.room

// Імпортуємо необхідні бібліотеки та класи
import androidx.room.*
import com.example.gymlog.data.local.legacy.DBHelper
import com.example.gymlog.data.local.room.dao.ExerciseInBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockDao
import com.example.gymlog.data.local.room.dao.TrainingBlockFilterDao
import com.example.gymlog.data.local.room.dao.WorkoutExerciseDao
import com.example.gymlog.data.local.room.dao.WorkoutGymDayDao
import com.example.gymlog.data.local.room.dao.WorkoutSetDao
import com.example.gymlog.data.local.room.entity.exercise.ExerciseEntity
import com.example.gymlog.data.local.room.entity.plan.GymDayEntity
import com.example.gymlog.data.local.room.entity.plan.PlanCycleEntity
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEquipmentEntity
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockExerciseEntity
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockMotionEntity
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockMuscleGroupEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity

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
)
abstract class WorkoutDatabase: RoomDatabase() {
    // Оголошуємо методи для отримання DAO (Data Access Object) для кожної таблиці
    abstract fun workoutGymDayDao(): WorkoutGymDayDao    // DAO для тренувальних днів
    abstract fun workoutSetDao(): WorkoutSetDao          // DAO для підходів
    abstract fun workoutExerciseDao(): WorkoutExerciseDao // DAO для вправ
    abstract fun trainingBlockDao(): TrainingBlockDao // DAO для тренув блоків
    abstract fun exerciseInBlockDao(): ExerciseInBlockDao //
    abstract fun trainingBlockFilterDao(): TrainingBlockFilterDao //
}









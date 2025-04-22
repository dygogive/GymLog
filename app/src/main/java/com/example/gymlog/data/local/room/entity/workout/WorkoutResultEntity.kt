package com.example.gymlog.data.local.room.entity.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "workout_result",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index( value = ["workoutExerciseId"] )
    ]
)

// WorkoutExercise - вправа що виконана
data class WorkoutResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?, //ід результату
    val workoutExerciseId: Long, // ід вправи WorkoutExercise
    val weight: Int?, // вага
    val iteration: Int?, // к-сть повторень
    val workTime: Int?, //тривалість виконання в секундах
    val orderInWorkoutExercise: Int, // який по черзі цей результат у цьому ж тренуванні
    val orderInWorkSet: Int, // який по черзі цей результат у цьому ж блоці WorkoutSet
    val orderInWorkGymDay: Int, // яке по черзі у всьому тренуванні
    val minutesSinceStartWorkout: Int, // минуло хвилин після старту тренування на момент запису цього результату
    val date: String
)
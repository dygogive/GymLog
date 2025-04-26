package com.example.gymlog.data.local.room.entity.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymlog.data.local.room.entity.exercise.ExerciseEntity


// WorkoutExercise.kt
@Entity(tableName = "WorkoutExercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutGymDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_gymday_ID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["workout_gymday_ID"]),
        Index(value = ["exerciseId"])
    ]
)
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?, //
    val workout_gymday_ID: Long?, // Тип Long для сумісності з батьком
    val exerciseId: Long?, // Тип Long для сумісності з батьком
    val name: String,
    val description: String?,
    val motion: String,
    val muscleGroups: String,
    val equipment: String,
    val comments: String? //записані коментарі
)


/*

// WorkoutExercise - вправа що виконана
data class WorkoutExercise(
    val id: Long?, //ід вправи
    val exerciseId: Long?, // ід вправи в списку вправ Exercise
    val name: String, // назва вправи
    val description: String?, //опис
    val motion: String, //тип руху
    val muscleGroups: String, //групи м'язів
    val equipment: String, //обладнання
    val comments: String? //записані коментарі
)
 */
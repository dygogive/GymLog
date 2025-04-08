package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey



// WorkoutExercise.kt
@Entity(tableName = "WorkoutExercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutGymDay::class,
            parentColumns = ["id"],
            childColumns = ["workout_gymday_ID"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workout_gymday_ID: Long, // Тип Long для сумісності з батьком
    val exerciseId: Long?,       // nullable, SET_NULL
    val name: String,
    val description: String?,
    val motion: String,
    val muscleGroups: String,
    val equipment: String,
    val isCustom: Int = 0
)
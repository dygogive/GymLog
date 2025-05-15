package com.example.gymlog.data.local.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_result",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["exerciseId"]),
        Index(value = ["programUuid"]),
        Index(value = ["trainingBlockUuid"])
    ]
)
data class WorkoutResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,  // Changed to nullable to match the existing schema

    val programUuid: String,              // 💡 Головна прив’язка — до Програми
    val trainingBlockUuid: String?,       // Вказує на копію блоку, звідки було виконання
    val exerciseId: Long,                 // Вправа, яка була виконана

    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,

    val sequenceInGymDay: Int,
    val position: Int,
    val timeFromStart: Long,
    val workoutDateTime: String  // Обов'язкове поле
)
package com.example.gymlog.data.local.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymlog.data.local.room.entities.plan.TrainingBlockExerciseEntity

@Entity(
    tableName = "workout_result",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlockExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseInBlockId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseInBlockId"])]
)
data class WorkoutResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,  // Changed to nullable to match the existing schema
    val exerciseInBlockId: Long,  // Посилання на TrainingBlockExerciseEntity
    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,
    val sequenceInGymDay: Int,
    val position: Int,
    val timeFromStart: Long,
    val workoutDateTime: String  // Обов'язкове поле
)
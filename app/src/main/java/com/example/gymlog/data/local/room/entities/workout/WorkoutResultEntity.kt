package com.example.gymlog.data.local.room.entities.workout

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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseInBlockId"])]
)
data class WorkoutResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseInBlockId: Long = 0,  // Посилання на TrainingBlockExerciseEntity
    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,
    val position: Int,
    val workoutDateTime: String,  // Додано нове поле
    val sequenceInGymDay: Int,
    val timeFromStart: Int
)
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
        Index(value = ["workoutExerciseId"])
    ]
)
data class WorkoutResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val workoutExerciseId: Long,
    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,
    val sequenceInGymDay: Int,
    val position: Int,
    val timeFromStart: Int
)
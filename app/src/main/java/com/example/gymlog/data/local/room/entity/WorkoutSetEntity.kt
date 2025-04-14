package com.example.gymlog.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// WorkoutSet.kt
@Entity(
    tableName = "WorkoutSet",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["tr_block_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = WorkoutGymDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["workout_id"]),
        Index(value = ["tr_block_id"])
    ]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val workout_id: Long?,
    val tr_block_id: Long?,
    val name: String?,
    val description: String?,
    val position: Int?
)
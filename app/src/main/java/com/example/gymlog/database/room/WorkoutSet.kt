package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// WorkoutSet.kt
@Entity(
    tableName = "WorkoutSet",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutGymDay::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrainingBlock::class,
            parentColumns = ["id"],
            childColumns = ["tr_block_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workout_id: Long,
    val tr_block_id: Long?, // Зміна на nullable
    val name: String,
    val description: String?,
    val position: Int
)
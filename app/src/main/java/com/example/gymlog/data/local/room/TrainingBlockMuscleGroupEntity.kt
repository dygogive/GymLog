package com.example.gymlog.data.local.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlockMuscleGroup",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["trainingBlockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["trainingBlockId"])
    ]
)
data class TrainingBlockMuscleGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val trainingBlockId: Long?,
    val muscleGroup: String?
)
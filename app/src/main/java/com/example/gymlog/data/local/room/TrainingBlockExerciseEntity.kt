package com.example.gymlog.data.local.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlockExercises",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["trainingBlockId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["trainingBlockId"]),
        Index(value = ["exerciseId"])
    ]
)
data class TrainingBlockExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val trainingBlockId: Long?,
    val exerciseId: Long?,
    val position: Int?
)

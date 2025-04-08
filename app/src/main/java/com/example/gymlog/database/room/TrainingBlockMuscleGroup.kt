package com.example.gymlog.database.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlockMuscleGroup",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlock::class,
            parentColumns = ["id"],
            childColumns = ["trainingBlockId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrainingBlockMuscleGroup(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trainingBlockId: Long,
    val muscleGroup: String
)
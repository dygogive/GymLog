package com.example.gymlog.database.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlockExercises",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlock::class,
            parentColumns = ["id"],
            childColumns = ["trainingBlockId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrainingBlockExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trainingBlockId: Long,
    val exerciseId: Long,
    val position: Int
)

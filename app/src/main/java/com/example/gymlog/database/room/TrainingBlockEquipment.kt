package com.example.gymlog.database.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlockEquipment",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlock::class,
            parentColumns = ["id"],
            childColumns = ["trainingBlockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["trainingBlockId"])
    ]
)
data class TrainingBlockEquipment(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val trainingBlockId: Long?,
    val equipment: String?
)
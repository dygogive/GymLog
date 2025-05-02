package com.example.gymlog.data.local.room.entities.plan

import androidx.room.*

@Entity(
    tableName = "TrainingBlockEquipment",
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
data class TrainingBlockEquipmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val trainingBlockId: Long?,
    val equipment: String?
)
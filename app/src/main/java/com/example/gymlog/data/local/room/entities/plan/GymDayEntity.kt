package com.example.gymlog.data.local.room.entities.plan

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "GymDays",
    foreignKeys = [ForeignKey(
        entity = PlanCycleEntity::class,
        parentColumns = ["id"],
        childColumns = ["plan_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["plan_id"])]  // ДОДАТИ ОЦЕ
)
data class GymDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val plan_id: Long,                      // NOT NULL → тип Long (без ?)
    val day_name: String,                   // NOT NULL
    val description: String?,
    val position: Int?
)

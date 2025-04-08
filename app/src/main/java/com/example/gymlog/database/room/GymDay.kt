package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "GymDays",
    foreignKeys = [
        ForeignKey(
            entity = PlanCycle::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GymDay(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plan_id: Long,
    val day_name: String,
    val description: String?,
    val position: Int
)

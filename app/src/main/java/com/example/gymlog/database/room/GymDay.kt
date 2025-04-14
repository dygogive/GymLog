package com.example.gymlog.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "GymDays",
    foreignKeys = [ForeignKey(
        entity = PlanCycle::class,
        parentColumns = ["id"],
        childColumns = ["plan_id"],
        onDelete = ForeignKey.CASCADE
    )],

)
data class GymDay(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val plan_id: Long,                      // NOT NULL → тип Long (без ?)
    val day_name: String,                   // NOT NULL
    val description: String?,
    val position: Int?
)

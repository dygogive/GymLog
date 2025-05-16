package com.example.gymlog.data.local.room.entities.plan

import androidx.room.*

@Entity(
    tableName = "TrainingBlock",
    foreignKeys = [ForeignKey(
        entity = GymDayEntity::class,
        parentColumns = ["id"],
        childColumns = ["gym_day_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["gym_day_id"])]  // І ТУТ ДОДАТИ
)
data class TrainingBlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val gym_day_id: Long,                   // NOT NULL
    val name: String,                       // NOT NULL
    val description: String?,
    val position: Int?,
    val uuid: String,
)
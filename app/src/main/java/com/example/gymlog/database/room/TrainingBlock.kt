package com.example.gymlog.database.room

import androidx.room.*

@Entity(
    tableName = "TrainingBlock",
    foreignKeys = [ForeignKey(
        entity = GymDay::class,
        parentColumns = ["id"],
        childColumns = ["gym_day_id"],
        onDelete = ForeignKey.CASCADE
    )],

)
data class TrainingBlock(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val gym_day_id: Long,                   // NOT NULL
    val name: String,                       // NOT NULL
    val description: String?,
    val position: Int?
)
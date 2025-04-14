package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "WorkoutGymDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanCycle::class,
            parentColumns = ["id"],
            childColumns = ["plansID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = GymDay::class,
            parentColumns = ["id"],
            childColumns = ["gymDaysID"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [                      // ← ДОДАЙ ОЦЕ
        Index(value = ["plansID"]),
        Index(value = ["gymDaysID"])
    ]
)
data class WorkoutGymDay(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val datetime: Long?,
    val plansID: Long?,
    val gymDaysID: Long?,
    val sets: Int?,
    val blocks: Int?,
    val minutes: Int?,
    val name: String?,
    val description: String?
)

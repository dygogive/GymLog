package com.example.gymlog.data.local.room.entities.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymlog.data.local.room.entities.plan.GymDayEntity
import com.example.gymlog.data.local.room.entities.plan.PlanCycleEntity

@Entity(
    tableName = "WorkoutGymDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanCycleEntity::class,
            parentColumns = ["id"],
            childColumns = ["plansID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = GymDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["gymDaysID"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["plansID"]),
        Index(value = ["gymDaysID"])
    ]
)
data class WorkoutGymDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val datetime: Long,
    val plansID: Long?,
    val gymDaysID: Long?,
    val minutes: Int?,
    val name: String,
    val description: String?
)
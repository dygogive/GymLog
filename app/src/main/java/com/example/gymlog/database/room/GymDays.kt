package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "GymDays",
    foreignKeys = [
        ForeignKey(
            entity =        PlanCycles::class, //Ентіті з батьківської таблиці
            parentColumns = ["id"], //батьківський ід
            childColumns =  ["plan_id"], //дочірній ід
            onDelete =      ForeignKey.CASCADE //якщо видалити батьківський рядок в PlanCycles то planCycleID = null
        )
    ]

)
data class GymDays(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plan_id:      Int,
    val day_name:     String,
    val description:  String,
    val position:     Int
)

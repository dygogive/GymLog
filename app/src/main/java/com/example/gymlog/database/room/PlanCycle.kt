package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "PlanCycles")
data class PlanCycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val creation_date: String,
    val position: Int,
    val is_active: Int = 0
)

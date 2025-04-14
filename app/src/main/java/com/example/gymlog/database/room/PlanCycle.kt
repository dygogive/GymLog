package com.example.gymlog.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "PlanCycles")
data class PlanCycle(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,                       // NOT NULL
    val description: String?,
    val creation_date: String?,             // nullable
    val position: Int?,
    @ColumnInfo(defaultValue = "0")         // <‑‑ Room тепер очікує DEFAULT 0
    val is_active: Int? = 0
)

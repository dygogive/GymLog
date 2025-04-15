package com.example.gymlog.data.local.room.dto

import androidx.room.ColumnInfo

data class ExerciseInBlockDto(
    @ColumnInfo(name = "linkId") val linkId: Long,
    @ColumnInfo(name = "exerciseId") val exerciseId: Long,
    val name: String,
    val description: String?,
    val motion: String?,
    val muscleGroups: String?,
    val equipment: String?,
    val isCustom: Boolean,
    val position: Int
)

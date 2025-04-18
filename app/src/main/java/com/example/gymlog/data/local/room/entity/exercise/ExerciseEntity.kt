package com.example.gymlog.data.local.room.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String?,
    val description: String?,
    val motion: String?,
    val muscleGroups: String?,
    val equipment: String?,
    val isCustom: Int? = 0
)
package com.example.gymlog.domain.model.plan

// domain/model/plan/FitnessProgramNew.kt
data class FitnessProgramNew(
    val name: String,
    val description: String?,
    val position: Int,
    val creationDate: String?,
    val gymDays: List<GymDayNew>
)

data class GymDayNew(
    val name: String,
    val description: String,
    val position: Int,
    val trainingBlocks: List<TrainingBlockNew>
)

data class TrainingBlockNew(
    val name: String,
    val description: String,
    val position: Int,
    val exercises: List<ExerciseInBlock>,
    val motions: List<Motion>,
    val muscleGroups: List<MuscleGroup>,
    val equipment: List<Equipment>
)
package com.example.gymlog.domain.model.workout

data class WorkoutResult(
    val id: Long? = null,
    val programUuid: String,
    val trainingBlockUuid: String?,  // Може бути null, якщо не прив'язано
    val exerciseId: Long,            // Це вже не exerciseInBlockId
    val weight: Int?,
    val iteration: Int?,
    val workTime: Int?,
    val position: Int = 0,
    val sequenceInGymDay: Int,
    val timeFromStart: Long,
    val workoutDateTime: String // Формат "yyyy.MM.dd HH:mm"
)
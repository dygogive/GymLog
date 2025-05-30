package com.example.gymlog.ui.feature.workout.model

//Один результат виконання сету
data class ResultOfSet(
    val id: Long? = 0,
    val programUuid: String = "null",
    var trainingBlockUuid: String? = "null",  // Може бути null, якщо не прив'язано
    var exerciseId: Long = -1,            // Це вже не exerciseInBlockId
    val weight:      Int?   = null,
    val iteration:   Int?   = null,
    val workTime:    Int?   = null,
    val currentDate: String = "00.00.0000",
    val currentTime: String = "00:00",
)

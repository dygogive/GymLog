package com.example.gymlog.ui.feature.workout.model

data class TimerParams(
    var totalTimeMs: Long,
    var lastSetTimeMs: Long,
    var buttonText: String = "button",
    val onStartStop: () -> Unit,
    val onSetFinish: () -> Unit,
    val isRunning: Boolean,
)

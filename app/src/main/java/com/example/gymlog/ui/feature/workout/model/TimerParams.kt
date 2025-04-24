package com.example.gymlog.ui.feature.workout.model

data class TimerParams(
    var totalTimeMs: Long,
    var lastSetTimeMs: Long,
    var buttonText: String = "button",
    val onStartStopClick: () -> Unit,
    val onSetFinished: () -> Unit,
    val isRunning: Boolean,
)

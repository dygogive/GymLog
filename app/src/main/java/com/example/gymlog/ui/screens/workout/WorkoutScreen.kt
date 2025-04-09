package com.example.gymlog.ui.screens.workout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun WorkoutScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Екран тренування!")
        // ... далі таймери, список тощо ...
    }
}
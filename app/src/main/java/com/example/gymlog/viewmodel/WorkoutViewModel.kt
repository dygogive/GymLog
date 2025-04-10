package com.example.gymlog.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gymlog.data.repository.WorkoutRepository
import com.example.gymlog.database.room.WorkoutDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // поки пусто — додамо пізніше StateFlow, таймери, роботу з Room
}

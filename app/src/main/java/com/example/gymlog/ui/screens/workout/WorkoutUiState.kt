package com.example.gymlog.ui.screens.workout

import com.example.gymlog.database.room.WorkoutSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class WorkoutUiState(
    val totalTimeMs: Long = 0L,
    val restTimeMs: Long = 0L,
    val sets: ImmutableList<WorkoutSet> = persistentListOf<WorkoutSet>(),

    // Додаткові можливі поля:
    val isRunning: Boolean = false,          // Чи активне тренування
    val currentSetId: Long? = null,         // Поточний активний сет
    val error: String? = null,              // Помилки
    val isLoading: Boolean = false          // Стан завантаження
) {
    // функція бере список WorkoutSet і функцією find, перебираючи кожен (it) знаходить потрібний, у якому id == currentSetId
    fun getCurrentSet(): WorkoutSet? = sets.find { it.id == currentSetId }
}

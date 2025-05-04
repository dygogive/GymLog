package com.example.gymlog.presentation

import android.content.Context
import com.example.gymlog.domain.usecase.GetProgramsForSelectionUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import javax.inject.Inject

/**
 * UseCase для отримання нових програм тренувань на рівні UI
 * Взаємодіє з доменним шаром та конвертує дані для відображення
 */
class FetchProgramsNewUiUseCase @Inject constructor(
    private val getProgramsForSelectionUseCase: GetProgramsForSelectionUseCase,
) {
    /**
     * Викликає доменний UseCase та перетворює результати на UI моделі
     */
    suspend operator fun invoke(context: Context): List<ProgramInfo> {
        // Викликаємо доменний UseCase, далі мапуємо кожну нову доменну модель в UI модель
        return getProgramsForSelectionUseCase()
            .map { fitnessProgramNew -> fitnessProgramNew.toUiModel(context) }
    }
}
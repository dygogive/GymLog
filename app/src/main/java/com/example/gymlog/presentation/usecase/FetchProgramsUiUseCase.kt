// FetchProgramsUiUseCase.kt
package com.example.gymlog.presentation.usecase

import android.content.Context
import com.example.gymlog.domain.usecase.gym_plan.GetFitnessProgramsUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import javax.inject.Inject

/**
 * UseCase для отримання програм тренувань на рівні UI
 * Взаємодіє з доменним шаром та конвертує дані для відображення
 */
class FetchProgramsUiUseCase @Inject constructor(
    private val getFitnessProgramsUseCase: GetFitnessProgramsUseCase,
) {
    /**
     * Викликає доменний UseCase та перетворює результати на UI моделі
     */
    suspend operator fun invoke(context: Context): List<ProgramInfo> {
        // Викликаємо доменний UseCase, далі мапуємо кожну доменну модель в UI модель
        return getFitnessProgramsUseCase()
            .map { fitnessProgram -> fitnessProgram.toUiModel(context) }
    }
}

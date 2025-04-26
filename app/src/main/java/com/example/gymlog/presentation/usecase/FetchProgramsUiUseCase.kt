package com.example.gymlog.presentation.usecase

import android.content.Context
import com.example.gymlog.domain.usecase.gym_plan.GetFitnessProgramsUseCase
import com.example.gymlog.presentation.mappers.toUiModel
import com.example.gymlog.ui.feature.workout.model.ProgramInfo
import javax.inject.Inject

/**
 * Presentation-layer UseCase: fetches domain FitnessPrograms and maps to UI ProgramInfo
 */
class FetchProgramsUiUseCase @Inject constructor(
    private val getFitnessProgramsUseCase: GetFitnessProgramsUseCase,
) {
    suspend operator fun invoke(context: Context): List<ProgramInfo> {
        // invoke domain use-case, then map each domain model to UI model
        return getFitnessProgramsUseCase()
            .map { fitnessProgram -> fitnessProgram.toUiModel(context) }
    }
}
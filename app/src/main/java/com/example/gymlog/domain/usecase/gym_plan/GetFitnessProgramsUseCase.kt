package com.example.gymlog.domain.usecase.gym_plan

import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity
import com.example.gymlog.domain.model.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.repository.FitnessProgramsInterface
import javax.inject.Inject

class GetFitnessProgramsUseCase @Inject constructor(
    private val repository: FitnessProgramsInterface
) {
    suspend operator fun invoke(): List<FitnessProgram> =
        repository.getFitnessPrograms()
}

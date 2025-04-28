package com.example.gymlog.domain.usecase.gym_plan

import com.example.gymlog.domain.model.plannew.FitnessProgramNew
import com.example.gymlog.domain.repository.FitnessProgramsInterface
import javax.inject.Inject

class GetFitnessProgramsNewUseCase @Inject constructor(
    private val repository: FitnessProgramsInterface
) {
    suspend operator fun invoke(): List<FitnessProgramNew> = repository.getFitnessProgramsNew()
}
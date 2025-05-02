package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject

class GetFitnessProgramsNewUseCase @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface
) {
    suspend operator fun invoke(): List<FitnessProgramNew> = repository.getAllFitnessProgramsNew()
}
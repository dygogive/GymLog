package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject

class GetProgramForGymUseCase @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface
){
    suspend operator fun invoke(idGymDay: Long): GymDayNew {






        return TODO()
    }
}
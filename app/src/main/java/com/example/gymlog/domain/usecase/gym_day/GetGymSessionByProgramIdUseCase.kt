package com.example.gymlog.domain.usecase.gym_day

import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.repository.GymSessionRepositoryInterface
import javax.inject.Inject


class GetGymSessionByProgramIdUseCase @Inject constructor(
    private val repository: GymSessionRepositoryInterface
) {
    suspend operator fun invoke(progId: Long): List<GymDay> =
        repository.getGymSessionByProgramId(progId)
}

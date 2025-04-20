package com.example.gymlog.domain.usecase.gym_day

import com.example.gymlog.domain.model.plan.Gym
import com.example.gymlog.domain.repository.GymSessionRepositoryInterface
import javax.inject.Inject


class GetGymSessionByProgramIdUseCase @Inject constructor(
    private val repository: GymSessionRepositoryInterface
) {
    suspend operator fun invoke(progId: Long): List<Gym> =
        repository.getGymSessionByProgramId(progId)
}

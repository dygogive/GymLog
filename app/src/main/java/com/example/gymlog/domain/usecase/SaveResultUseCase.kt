package com.example.gymlog.domain.usecase


import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject

class SaveResultUseCase @Inject constructor(
    private val fitnessProgramNewRepositoryInterface: FitnessProgramNewRepositoryInterface,
    private val workoutResultRepositoryInterface: WorkoutResultRepositoryInterface,
) {
    suspend operator fun invoke(
        exerciseInBlockId: Long,
        weight: Int?,
        iterations: Int?,
        workTime: Int?,
        sequenceInGymDay: Int,
        timeFromStart: Long,
        workoutDateTime: String
        ): GymDayNew {






        return TODO()
    }
}
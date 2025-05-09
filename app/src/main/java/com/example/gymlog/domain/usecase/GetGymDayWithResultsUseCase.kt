package com.example.gymlog.domain.usecase

import com.example.gymlog.data.repository.WorkoutResultRepository
import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import com.example.gymlog.domain.repository.WorkoutResultRepositoryInterface
import javax.inject.Inject

class GetGymDayWithResultsUseCase  @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface,
    private val workoutResultRepository: WorkoutResultRepositoryInterface
) {
    suspend operator fun invoke(
        gymDayId: Long,
        maxResultsPerExercise: Int
    ): GymDayNew {

        //get needed GymDay from repository
        val gymDay = repository.getSelectedGymDayNew(gymDayId)

        //insert results in GymDay
        return gymDay.copy(
            trainingBlocks = gymDay.trainingBlocks.map { block ->
                block.copy(
                    exercises = block.exercises.map { exercise ->
                        val results = workoutResultRepository.getBestUniqueResults(
                            exerciseInBlockId = exercise.linkId,
                            resultsNumber = maxResultsPerExercise
                        )
                        exercise.copy(workoutResults = results)
                    }
                )
            }
        )
    }
}
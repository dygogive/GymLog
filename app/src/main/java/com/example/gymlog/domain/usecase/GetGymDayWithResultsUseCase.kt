package com.example.gymlog.domain.usecase

import com.example.gymlog.domain.model.plan.GymDayNew
import com.example.gymlog.domain.repository.FitnessProgramNewRepositoryInterface
import javax.inject.Inject

class GetGymDayWithResultsUseCase  @Inject constructor(
    private val repository: FitnessProgramNewRepositoryInterface,
    private val getBestUniqueResultsUseCase: GetBestUniqueResultsUseCase
) {
    suspend operator fun invoke(
        programUuid: String,
        gymDayId: Long,
        maxResultsPerExercise: Int,
        currentWorkoutDateTime: String? = "no data and time"
    ): GymDayNew {

        //get needed GymDay from repository
        val gymDay = repository.getSelectedGymDayNew(gymDayId)

        //insert results in GymDay
        return gymDay.copy(
            trainingBlocks = gymDay.trainingBlocks.map { block ->
                block.copy(
                    exercises = block.exercises.map { exercise ->
                        val results = getBestUniqueResultsUseCase(
                            programUuid = programUuid,
                            exerciseId = exercise.exerciseId,
                            trainingBlockUuid = block.uuid,
                            resultsNumber = maxResultsPerExercise,
                            currentWorkoutDateTime = currentWorkoutDateTime
                        )
                        exercise.copy(workoutResults = results)
                    }
                )
            }
        )
    }
}
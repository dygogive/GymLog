package com.example.gymlog.domain.repository



import com.example.gymlog.domain.model.workout.WorkoutGymDay


interface WorkoutRepositoryInterface {
    suspend fun updateWorkoutGymDay(workoutGymDay: WorkoutGymDay): Long

}

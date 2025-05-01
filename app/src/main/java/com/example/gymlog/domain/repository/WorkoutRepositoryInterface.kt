package com.example.gymlog.domain.repository



import com.example.gymlog.domain.model.workout.WorkoutGymDay


interface WorkoutRepositoryInterface {
    suspend fun insertWorkoutGymDay(workoutGymDay: WorkoutGymDay): Long
    suspend fun getWorkoutGymDays(): List<WorkoutGymDay>
}

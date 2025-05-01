package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutResultEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.model.workout.WorkoutResult
import com.example.gymlog.domain.model.workout.WorkoutSet

fun WorkoutSetEntity.toDomain(exercises: List<WorkoutExercise> = emptyList(), results: List<WorkoutResult> = emptyList()) = WorkoutSet(
    name = name,
    description = description,
    workoutExercises = exercises,
    muscleGroups = emptyList(), // Need to map from entity when available
    equipment = equipment ?: throw IllegalStateException("Equipment cannot be null"),
    results = results,
    position = position
)

fun WorkoutSet.toEntity() = WorkoutSetEntity(
    id = id ?: 0, // Assuming id field is still present but nullable in domain model
    workout_id = workoutId ?: 0, // Need to align with domain model
    tr_block_id = trainingBlockId ?: 0, // Need to align with domain model
    name = name,
    description = description,
    position = position,
    physicalСondition = null, // Field removed from domain model
    comments = null, // Field removed from domain model
    equipment = equipment
)

fun WorkoutGymDayEntity.toDomain(workoutSets: List<WorkoutSet> = emptyList()) = WorkoutGymDay(
    name = name,
    description = description,
    workoutSets = workoutSets,
    minutes = minutes,
    datetime = datetime
)

fun WorkoutGymDay.toEntity() = WorkoutGymDayEntity(
    id = id ?: 0, // Assuming id field is still present but nullable in domain model
    datetime = datetime,
    plansID = null, // Field removed from domain model
    gymDaysID = null, // Field removed from domain model
    sets = null, // Field removed from domain model
    blocks = null, // Field removed from domain model
    minutes = minutes,
    name = name,
    description = description,
    physicalСondition = null, // Field removed from domain model
    comments = null // Field removed from domain model
)

fun WorkoutExerciseEntity.toDomain(results: List<WorkoutResult> = emptyList()) = WorkoutExercise(
    name = name,
    description = description,
    motion = motion ?: throw IllegalStateException("Motion cannot be null"),
    muscleGroups = muscleGroups ?: emptyList(),
    equipment = equipment ?: throw IllegalStateException("Equipment cannot be null"),
    results = results,
    position = position
)

fun WorkoutExercise.toEntity() = WorkoutExerciseEntity(
    id = id ?: 0, // Assuming id field is still present but nullable in domain model
    workout_gymday_ID = workoutGymDayId ?: 0, // Need to align with domain model
    exerciseId = exerciseId ?: 0, // Need to align with domain model
    name = name,
    description = description,
    motion = motion,
    muscleGroups = muscleGroups,
    equipment = equipment,
    comments = null, // Field removed from domain model
    position = position
)

fun WorkoutResultEntity.toDomain() = WorkoutResult(
    weight = weight,
    iteration = iteration,
    workTime = workTime,
    sequenceInGymDay = orderInWorkGymDay ?: 0, // Renamed field
    position = orderInWorkoutExercise ?: 0, // Using orderInWorkoutExercise as position
    timeFromStart = minutesSinceStartWorkout ?: 0 // Renamed field
)

fun WorkoutResult.toEntity() = WorkoutResultEntity(
    id = id ?: 0, // Assuming id field is still present but nullable in domain model
    workoutExerciseId = workoutExerciseId ?: 0, // Need to align with domain model
    weight = weight,
    iteration = iteration,
    workTime = workTime,
    orderInWorkoutExercise = position, // Mapped from position
    orderInWorkSet = null, // Field removed from domain model
    orderInWorkGymDay = sequenceInGymDay, // Mapped from sequenceInGymDay
    minutesSinceStartWorkout = timeFromStart, // Mapped from timeFromStart
    date = null // Field removed from domain model
)
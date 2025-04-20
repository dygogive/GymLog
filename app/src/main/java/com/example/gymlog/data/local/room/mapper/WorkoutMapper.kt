package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.entity.workout.WorkoutExerciseEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutGymDayEntity
import com.example.gymlog.data.local.room.entity.workout.WorkoutSetEntity
import com.example.gymlog.domain.model.workout.WorkoutExercise
import com.example.gymlog.domain.model.workout.WorkoutGymDay
import com.example.gymlog.domain.model.workout.WorkoutSet

fun WorkoutSetEntity.toDomain() = WorkoutSet(
    id = id,
    workoutId = workout_id,
    trainingBlockId = tr_block_id,
    name = name,
    description = description,
    position = position,
    physicalСondition = physicalСondition,
    comments = comments,
)

fun WorkoutSet.toEntity() = WorkoutSetEntity(
    id = id,
    workout_id = workoutId,
    tr_block_id = trainingBlockId,
    name = name,
    description = description,
    position = position,
    physicalСondition = physicalСondition,
    comments = comments
)

fun WorkoutGymDayEntity.toDomain() = WorkoutGymDay(
    id = id,
    datetime = datetime,
    plansId = plansID,
    gymDaysId = gymDaysID,
    sets = sets,
    blocks = blocks,
    minutes = minutes,
    name = name,
    description = description,
    physicalСondition = physicalСondition,
    comments = comments
)

fun WorkoutGymDay.toEntity() = WorkoutGymDayEntity(
    id = id,
    datetime = datetime,
    plansID = plansId,
    gymDaysID = gymDaysId,
    sets = sets,
    blocks = blocks,
    minutes = minutes,
    name = name,
    description = description,
    physicalСondition = physicalСondition,
    comments = comments
)

fun WorkoutExerciseEntity.toDomain() = WorkoutExercise(
    id = id,
    workoutGymDayId = workout_gymday_ID,
    exerciseId = exerciseId,
    name = name,
    description = description,
    motion = motion,
    muscleGroups = muscleGroups,
    equipment = equipment,
    weight = weight,
    iteration = iteration,
    worktime = worktime,
    orderInWorkSet = orderInWorkSet,
    orderInWorkGymDay = orderInWorkGymDay,
    minutesSinceStartWorkout = minutesSinceStartWorkout,
)

fun WorkoutExercise.toEntity() = WorkoutExerciseEntity(
    id = id,
    workout_gymday_ID = workoutGymDayId,
    exerciseId = exerciseId,
    name = name,
    description = description,
    motion = motion,
    muscleGroups = muscleGroups,
    equipment = equipment,
    weight = weight,
    iteration = iteration,
    worktime = worktime,
    orderInWorkSet = orderInWorkSet,
    orderInWorkGymDay = orderInWorkGymDay,
    minutesSinceStartWorkout = minutesSinceStartWorkout,
)
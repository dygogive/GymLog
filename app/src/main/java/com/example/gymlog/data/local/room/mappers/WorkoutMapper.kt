package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.data.local.room.entities.WorkoutResultEntity
import com.example.gymlog.domain.model.workout.WorkoutResult


// Entity to Domain
fun WorkoutResultEntity.toDomain(): WorkoutResult = WorkoutResult(
    id = this.id,
    programUuid = this.programUuid,
    trainingBlockUuid = this.trainingBlockUuid,
    exerciseId = this.exerciseId,
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    sequenceInGymDay = this.sequenceInGymDay,
    position = this.position,
    timeFromStart = this.timeFromStart,
    workoutDateTime = this.workoutDateTime
)



// Domain to Entity
fun WorkoutResult.toEntity(): WorkoutResultEntity = WorkoutResultEntity(
    id = this.id, // якщо null — Room сам створить
    programUuid = this.programUuid,
    trainingBlockUuid = this.trainingBlockUuid,
    exerciseId = this.exerciseId,
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    sequenceInGymDay = this.sequenceInGymDay,
    position = this.position,
    timeFromStart = this.timeFromStart,
    workoutDateTime = this.workoutDateTime
)



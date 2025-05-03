package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.data.local.room.entities.workout.*
import com.example.gymlog.domain.model.workout.*



// Entity to/from Domain
fun WorkoutResultEntity.toDomain(): WorkoutResult = WorkoutResult(
    id = this.id,
    exerciseInBlockId = this.exerciseInBlockId,
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    sequenceInGymDay = this.sequenceInGymDay,
    position = this.position,
    timeFromStart = this.timeFromStart,
    workoutDateTime = this.workoutDateTime
)

fun WorkoutResult.toEntity(): WorkoutResultEntity = WorkoutResultEntity(
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    sequenceInGymDay = this.sequenceInGymDay,
    position = this.position,
    timeFromStart = this.timeFromStart,
    workoutDateTime = this.workoutDateTime
)


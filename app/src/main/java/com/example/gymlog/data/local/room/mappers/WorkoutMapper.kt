package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.data.local.room.entities.workout.*
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.workout.*

// Generic enum serialization helpers
private inline fun <reified T : Enum<T>> List<T>.serializeEnums(): String =
    this.joinToString(",") { it.name }

private inline fun <reified T : Enum<T>> String.deserializeToEnums(): List<T> =
    if (this.isBlank()) emptyList()
    else this.split(",").mapNotNull {
        try { enumValueOf<T>(it.trim()) }
        catch (e: IllegalArgumentException) { null }
    }

// WorkoutSet mapper
fun WorkoutSetEntity.toDomain(
    exercises: List<WorkoutExercise> = emptyList(),
    results: List<WorkoutResult> = emptyList()
) = WorkoutSet(
    name = name,
    description = description,
    workoutExercises = exercises,
    muscleGroups = muscleGroups.deserializeToEnums(),
    equipments = equipments.deserializeToEnums(),
    motions = motions.deserializeToEnums(),
    results = results,
    position = position
)

fun WorkoutSet.toEntity(workoutId: Long) = WorkoutSetEntity(
    id = null,
    workout_id = workoutId,
    tr_block_id = null,
    name = name,
    description = description,
    muscleGroups = muscleGroups.serializeEnums(),
    motions = motions.serializeEnums(),
    equipments = equipments.serializeEnums(),
    position = position
)

// WorkoutGymDay mapper
fun WorkoutGymDayEntity.toDomain(
    workoutSets: List<WorkoutSet> = emptyList()
) = WorkoutGymDay(
    name = name,
    description = description,
    workoutSets = workoutSets,
    minutes = minutes,
    datetime = datetime
)

fun WorkoutGymDay.toEntity() = WorkoutGymDayEntity(
    id = null,
    datetime = datetime,
    plansID = null,
    gymDaysID = null,
    minutes = minutes,
    name = name,
    description = description
)

// WorkoutExercise mapper
fun WorkoutExerciseEntity.toDomain(
    results: List<WorkoutResult> = emptyList()
) = WorkoutExercise(
    name = name,
    description = description,
    motion = MotionNew.valueOf(motion),
    muscleGroups = muscleGroups.deserializeToEnums(),
    equipment = EquipmentNew.valueOf(equipment),
    results = results,
    position = position
)

fun WorkoutExercise.toEntity(workoutSetId: Long) = WorkoutExerciseEntity(
    id = null,
    workout_set_id = workoutSetId,
    exerciseId = null,
    name = name,
    description = description,
    motion = motion.name,
    muscleGroups = muscleGroups.serializeEnums(),
    equipment = equipment.name,
    position = position
)

// WorkoutResult mapper remains the same
fun WorkoutResultEntity.toDomain() = WorkoutResult(
    weight = weight,
    iteration = iteration,
    workTime = workTime,
    sequenceInGymDay = sequenceInGymDay,
    position = position,
    timeFromStart = timeFromStart
)

fun WorkoutResult.toEntity(workoutExerciseId: Long) = WorkoutResultEntity(
    id = null,
    workoutExerciseId = workoutExerciseId,
    weight = weight,
    iteration = iteration,
    workTime = workTime,
    sequenceInGymDay = sequenceInGymDay,
    position = position,
    timeFromStart = timeFromStart
)
// PresentationMappers.kt
package com.example.gymlog.presentation.mappers

import android.content.Context
import com.example.gymlog.domain.model.plan.*
import com.example.gymlog.domain.model.exercise.*
import com.example.gymlog.domain.model.workout.*
import com.example.gymlog.ui.feature.workout.model.*

/**
 * Маппери для конвертації доменних моделей у UI моделі
 */

/**
 * Конвертація блоку тренування з доменної моделі в UI модель
 */
fun TrainingBlock.toUiModel(context: Context): TrainingBlockUiModel = TrainingBlockUiModel(
    name = this.name,
    description = this.description,
    attributesInfo = AttributesInfo(
        motionStateList = MotioStateList(this.motions.map { it.getDescription(context) }),
        muscleStateList = MusclesStateList(this.muscleGroupList.map { it.getDescription(context) }),
        equipmentStateList = EquipmentStateList(this.equipmentList.map { it.getDescription(context) }),
    ),
    infoExercises = this.exercises.map { it.toUiModel(context) }
)

/**
 * Конвертація вправи з доменної моделі в UI модель
 */
fun ExerciseInBlock.toUiModel(context: Context): ExerciseInfo = ExerciseInfo(
    name = this.getNameOnly(context),
    description = this.description,
    results = emptyList()  // Початково результати відсутні 
)

/**
 * Конвертація дня тренування з доменної моделі в UI модель
 */
fun GymDay.toUiModel(context: Context): GymDayUiModel = GymDayUiModel(
    name = this.name,
    description = this.description,
    position = this.position,
    trainingBlockUiModels = this.trainingBlocks.map { it.toUiModel(context) }
)

/**
 * Конвертація програми тренувань з доменної моделі в UI модель
 */
fun FitnessProgram.toUiModel(context: Context): ProgramInfo = ProgramInfo(
    name = this.name,
    description = this.description,
    gymDayUiModels = this.gymSessions.map { it.toUiModel(context) }
)

/**
 * Конвертація фільтрів блоків з доменної моделі в UI модель атрибутів
 */
fun BlockFilters.toUiModel(context: Context): AttributesInfo = AttributesInfo(
    motionStateList = MotioStateList(this.motions.map { it.getDescription(context) }),
    muscleStateList = MusclesStateList(this.muscleGroups.map { it.getDescription(context) }),
    equipmentStateList = EquipmentStateList(this.equipment.map { it.getDescription(context) })
)

/**
 * Конвертація вправи тренування з доменної моделі в UI модель з результатами
 */
fun WorkoutExercise.toUiModel(results: List<WorkoutResult>): ExerciseInfo = ExerciseInfo(
    name = this.name,
    description = this.description ?: "",
    results = results.map { it.toUiModel() }
)

/**
 * Конвертація результату тренування з доменної моделі в UI модель
 */
fun WorkoutResult.toUiModel(): ResultOfSet = ResultOfSet(
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    currentDate = this.date,
    currentTime = this.date  // або окреме поле часу, якщо існує
)
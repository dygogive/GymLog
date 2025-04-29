package com.example.gymlog.presentation.mappers

import android.content.Context
import com.example.gymlog.domain.model.plannew.*
import com.example.gymlog.domain.model.exercisenew.*
import com.example.gymlog.domain.model.attributenew.*
import com.example.gymlog.ui.feature.workout.model.*

/**
 * Маппери для конвертації нових доменних моделей у UI моделі
 */

/**
 * Конвертація нового блоку тренування з доменної моделі в UI модель
 */
fun TrainingBlockNew.toUiModel(context: Context): TrainingBlockUiModel = TrainingBlockUiModel(
    name = this.name,
    description = this.description,
    attributesInfo = AttributesInfo(
        motionStateList = MotioStateList(this.motions.map { it.getDescription(context) }),
        muscleStateList = MusclesStateList(this.muscleGroups.map { it.getDescription(context) }),
        equipmentStateList = EquipmentStateList(this.equipment.map { it.getDescription(context) }),
    ),
    infoExercises = this.exercises.map { it.toUiModel(context) }
)

/**
 * Конвертація нової вправи з доменної моделі в UI модель
 */
fun ExerciseInBlockNew.toUiModel(context: Context): ExerciseInfo = ExerciseInfo(
    name = this.getNameOnly(context),
    description = this.description,
    results = emptyList()  // Початково результати відсутні
)

/**
 * Допоміжна функція для отримання імені вправи
 */
fun ExerciseInBlockNew.getNameOnly(context: Context): String {
    return this.name
}

/**
 * Допоміжна функція для отримання локалізованого опису руху
 */
fun MotionNew.getDescription(context: Context): String {
    val resourceId = MotionNewMapper.mapToStringResource(this)
    return context.getString(resourceId)
}

/**
 * Допоміжна функція для отримання локалізованого опису групи м'язів
 */
fun MuscleGroupNew.getDescription(context: Context): String {
    val resourceId = MuscleGroupNewMapper.mapToStringResource(this)
    return context.getString(resourceId)
}

/**
 * Допоміжна функція для отримання локалізованого опису обладнання
 */
fun EquipmentNew.getDescription(context: Context): String {
    val resourceId = EquipmentNewMapper.mapToStringResource(this)
    return context.getString(resourceId)
}

/**
 * Конвертація нового дня тренування з доменної моделі в UI модель
 */
fun GymDayNew.toUiModel(context: Context): GymDayUiModel = GymDayUiModel(
    name = this.name,
    description = this.description,
    position = this.position?: 0,
    trainingBlockUiModels = this.trainingBlocks.map { it.toUiModel(context) }
)

/**
 * Конвертація нової програми тренувань з доменної моделі в UI модель
 */
fun FitnessProgramNew.toUiModel(context: Context): ProgramInfo = ProgramInfo(
    name = this.name,
    description = this.description,
    gymDayUiModels = this.gymDays.map { it.toUiModel(context) }
)
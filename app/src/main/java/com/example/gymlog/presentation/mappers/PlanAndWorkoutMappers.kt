package com.example.gymlog.presentation.mappers

import android.content.Context
import com.example.gymlog.domain.model.attribute.*
import com.example.gymlog.domain.model.exercise.*
import com.example.gymlog.domain.model.plan.*
import com.example.gymlog.domain.model.workout.WorkoutResult
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
        motionStateList = MotionStateList(this.motions.map { it.getDescription(context) }),
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
 * Допоміжна функція для отримання імені вправи, якщо name - це ідентифікатор ресурсу
 * (наприклад, "equipment_barbell" для <string name="equipment_barbell">Barbell</string>)
 */
fun ExerciseInBlockNew.getNameOnly(context: Context): String {
    val value: String = name

    if (!isCustom) {
        try {
            // Шукаємо ресурс за його ідентифікатором (ключем)
            val resId = context.resources.getIdentifier(
                value,         // Ідентифікатор ресурсу, наприклад "equipment_barbell"
                "string",      // Тип ресурсу
                context.packageName  // Пакет
            )

            // Якщо ресурс знайдено, повертаємо його значення
            if (resId != 0) {
                return context.getString(resId)
            }

            // Якщо ресурс не знайдено, повертаємо оригінальне значення
            return value
        } catch (e: Exception) {
            // У випадку будь-якої помилки повертаємо оригінальне значення
            return name
        }
    } else {
        // Якщо не кастомна вправа, просто повертаємо назву
        return name
    }
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
    trainingBlocksUiModel = this.trainingBlocks.map { it.toUiModel(context) }
)

/**
 * Конвертація нової програми тренувань з доменної моделі в UI модель
 */
fun FitnessProgramNew.toUiModel(context: Context): ProgramInfo = ProgramInfo(
    name = this.name,
    description = this.description,
    gymDayUiModels = this.gymDays.map { it.toUiModel(context) }
)



// Domain to UI
fun WorkoutResult.toUiModel(): ResultOfSet = ResultOfSet(
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    currentDate = this.workoutDateTime.substringBefore(" "),
    currentTime = this.workoutDateTime.substringAfter(" ")
)
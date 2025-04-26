package com.example.gymlog.presentation.mappers

import android.content.Context
import com.example.gymlog.domain.model.plan.*
import com.example.gymlog.domain.model.exercise.*
import com.example.gymlog.domain.model.workout.*
import com.example.gymlog.ui.feature.workout.model.*

/**
 * Extension function
 * Mapper for domain.model.plan.TrainingBlock -> UI TrainingBlockInfo
 */
fun TrainingBlock.toUiModel(context: Context): TrainingBlockUI = TrainingBlockUI(
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
 * Mapper for ExerciseInBlock -> UI ExerciseInfo
 */
fun ExerciseInBlock.toUiModel(context: Context): ExerciseInfo = ExerciseInfo(
    name = this.getNameOnly(context),
    description = this.description,
    results = emptyList()
)



/**
 * Mapper for domain.model.plan.GymDay -> UI GymDayInfo (for selection list)
 */
fun GymDay.toUiModel(): GymDayInfo = GymDayInfo(
    name = this.name,
    description = this.description
)


/**
 * Mapper for domain.model.plan.FitnessProgram -> UI ProgramInfo (for selection list)
 */

fun FitnessProgram.toUiModel(): ProgramInfo = ProgramInfo(
    name = this.name,
    description = this.description
)





/**
 * Mapper for domain.model.plan.BlockFilters -> UI FiltersInfo
 */
fun BlockFilters.toUiModel(context: Context): AttributesInfo = AttributesInfo(
    motionStateList = MotioStateList (this.motions.map{ it.getDescription(context) } ),
    muscleStateList = MusclesStateList (this.muscleGroups.map { it.getDescription(context) }),
    equipmentStateList = EquipmentStateList(this.equipment.map { it.getDescription(context) })
)



/**
 * Mapper for domain.model.workout.WorkoutExercise -> UI ExerciseInfo with results
 */
fun WorkoutExercise.toUiModel(results: List<WorkoutResult>): ExerciseInfo = ExerciseInfo(
    name = this.name,
    description = this.description?: "",
    results = results.map { it.toUiModel() }
)



/**
 * Mapper for domain.model.workout.WorkoutResult -> UI ResultOfSet
 */
fun WorkoutResult.toUiModel(): ResultOfSet = ResultOfSet(
    weight = this.weight,
    iteration = this.iteration,
    workTime = this.workTime,
    currentDate = this.date,
    currentTime = this.date // or separate time field if exists
)




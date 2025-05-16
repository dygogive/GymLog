package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.data.local.room.entities.plan.TrainingBlockEntity
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew
import com.example.gymlog.domain.model.legacy.plan.TrainingBlock
import com.example.gymlog.domain.model.plan.TrainingBlockNew
import com.example.gymlog.domain.model.exercise.ExerciseInBlockNew

fun TrainingBlockEntity.toDomain(): TrainingBlock {
    return TrainingBlock(
        id ?: 0,
        gym_day_id,
        name,
        description,
        emptyList<Motion>(),                  // motions
        emptyList<MuscleGroup>(),               // muscleGroupList
        emptyList<Equipment>(),               // equipmentList
        position ?: 0,
        emptyList<ExerciseInBlock>(),           // exercises
        uuid
    )
}

fun TrainingBlock.toEntity(): TrainingBlockEntity {
    return TrainingBlockEntity(
        id = if (this.id == 0L) null else this.id,
        gym_day_id = this.gymDayId,
        name = this.name,
        description = this.description,
        position = this.position,
        uuid = this.uuid
    )
}

fun TrainingBlockEntity.toDomainNew(
    exercises: List<ExerciseInBlockNew> = emptyList(),
    motions: List<MotionNew> = emptyList(),
    equipment: List<EquipmentNew> = emptyList(),
    muscleGroup: List<MuscleGroupNew> = emptyList()
): TrainingBlockNew {
    return TrainingBlockNew(
        id = id ?: 0,
        uuid = uuid,
        name = name,
        description = description ?: "",
        position = position,
        exercises = exercises,
        motions = motions,
        muscleGroups = muscleGroup,
        equipment = equipment
    )
}
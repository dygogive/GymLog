package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.TrainingBlock


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
        emptyList<ExerciseInBlock>()           // exercises
    )
}

fun TrainingBlock.toEntity(): TrainingBlockEntity {
    return TrainingBlockEntity(
        id = if (this.id == 0L) null else this.id,
        gym_day_id = this.gymDayId,
        name = this.name,
        description = this.description,
        position = this.position
    )
}

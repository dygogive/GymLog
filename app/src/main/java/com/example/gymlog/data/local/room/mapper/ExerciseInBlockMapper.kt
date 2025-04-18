package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.dto.ExerciseInBlockDto
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.core.utils.parseEnumList
import com.example.gymlog.core.utils.parseEnumOrNull

fun ExerciseInBlockDto.toDomain(): ExerciseInBlock {
    return ExerciseInBlock(
        linkId,
        exerciseId,
        name,
        description,
        parseEnumOrNull<Motion>(motion),
        parseEnumList<MuscleGroup>(muscleGroups),
        parseEnumOrNull<Equipment>(equipment),
        position
    )
}


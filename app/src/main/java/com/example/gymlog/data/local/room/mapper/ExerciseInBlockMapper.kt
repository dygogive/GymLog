package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.dto.ExerciseInBlockDto
import com.example.gymlog.domain.model.exercise.Equipment
import com.example.gymlog.domain.model.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.exercise.Motion
import com.example.gymlog.domain.model.exercise.MuscleGroup
import com.example.gymlog.utils.parseEnumList
import com.example.gymlog.utils.parseEnumOrNull

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


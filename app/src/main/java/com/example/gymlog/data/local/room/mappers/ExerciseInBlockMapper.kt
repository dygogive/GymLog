package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.core.utils.EnumMapper
import com.example.gymlog.data.local.room.dto.ExerciseInBlockDto
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup
import com.example.gymlog.core.utils.parseEnumList
import com.example.gymlog.core.utils.parseEnumOrNull
import com.example.gymlog.domain.model.exercise.ExerciseInBlockNew
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew



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




fun ExerciseInBlockDto.toDomainNew(): ExerciseInBlockNew {
    return ExerciseInBlockNew(
        name = name,
        description = description ?: "",
        motion = EnumMapper.fromString(motion, MotionNew.PRESS_BY_LEGS),
        muscleGroups = EnumMapper.fromStringList(muscleGroups, MuscleGroupNew.CHEST),
        equipment = EnumMapper.fromString(equipment, EquipmentNew.BARBELL),
        isCustom = isCustom,
        position = position,
    )
}


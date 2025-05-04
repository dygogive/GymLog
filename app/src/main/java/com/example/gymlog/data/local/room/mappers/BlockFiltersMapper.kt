package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.core.utils.parseEnumList
import com.example.gymlog.core.utils.parseEnumOrNull
import com.example.gymlog.data.local.room.dto.BlockFiltersDto
import com.example.gymlog.domain.model.attribute.BlockFilters
import com.example.gymlog.domain.model.attribute.EquipmentNew
import com.example.gymlog.domain.model.attribute.MotionNew
import com.example.gymlog.domain.model.attribute.MuscleGroupNew

object BlockFiltersMapper {

    fun BlockFiltersDto.toDomain(): BlockFilters {
        return BlockFilters(
            motions = motions.mapNotNull { parseEnumOrNull<MotionNew>(it) },
            muscleGroups = muscleGroups.flatMap { parseEnumList<MuscleGroupNew>(it) },
            equipment = equipment.mapNotNull { parseEnumOrNull<EquipmentNew>(it) }
        )
    }
}
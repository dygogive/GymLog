package com.example.gymlog.data.local.room.mappers

import com.example.gymlog.data.local.room.entities.plan.PlanCycleEntity
import com.example.gymlog.domain.model.legacy.plan.FitnessProgram
import com.example.gymlog.domain.model.plan.FitnessProgramNew
import com.example.gymlog.domain.model.plan.GymDayNew


fun PlanCycleEntity.toDomain() = FitnessProgram(
    id?: -1,
    name,
    description,
    creation_date,
    position?: -1,
    is_active?: 0,
    uuid
)

fun FitnessProgram.toEntity() = PlanCycleEntity(
    id = if (this.id == -1L) null else this.id,
    name = this.name,
    description = this.description,
    creation_date = this.creation_date,
    position = this.position,
    is_active = this.is_active,
    uuid = this.uuid
)



fun PlanCycleEntity.toDomainNew(
    gymDays: List<GymDayNew> = emptyList()
): FitnessProgramNew = FitnessProgramNew(
    id = id ?: 0,
    name = name,
    description = description,
    position = position,
    creationDate = creation_date,
    gymDays = gymDays,
    uuid = uuid
)
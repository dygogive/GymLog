package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.entity.plan.PlanCycleEntity
import com.example.gymlog.domain.model.plan.FitnessProgram

fun PlanCycleEntity.toDomain() = FitnessProgram(
    id?: -1,
    name,
    description,
    creation_date,
    position?: -1,
    is_active?: 0
)

fun FitnessProgram.toEntity() = PlanCycleEntity(
    id = if (this.id == -1L) null else this.id,
    name = this.name,
    description = this.description,
    creation_date = this.creation_date,
    position = this.position,
    is_active = this.is_active
)

package com.example.gymlog.data.local.room.mapper

import com.example.gymlog.data.local.room.entity.plan.GymDayEntity
import com.example.gymlog.domain.model.plan.GymDay
import com.example.gymlog.domain.model.plan.TrainingBlock
import com.example.gymlog.domain.model.plannew.GymDayNew
import com.example.gymlog.domain.model.plannew.TrainingBlockNew

/**
 * Перетворює GymDayEntity у GymDay.
 * @param blocks список блоків тренувань — за замовчуванням порожній.
 */
fun GymDayEntity.toDomain(
    blocks: List<TrainingBlock> = emptyList()
): GymDay = GymDay(
    /* id */           this.id ?: 0L,
    /* fitProgramId */ this.plan_id,
    /* name */         this.day_name,
    /* description */  this.description ?: "",
    /* position */     this.position ?: -1,
    /* trainingBlocks*/ blocks
)


/**
 * Перетворює GymDay (домен) назад у GymDayEntity (для збереження в БД).
 * – Якщо id ≤ 0, вважаємо, що це новий запис, й передаємо null (авто‑генерація).
 * – Якщо description порожній, зберігаємо null.
 * – Якщо position < 0, зберігаємо null.
 */
fun GymDay.toEntity(): GymDayEntity = GymDayEntity(
    id          = id.takeIf { it > 0L },
    plan_id     = planId.toLong(),
    day_name    = name,
    description = description.takeIf { it.isNotBlank() },
    position    = position.takeIf { it >= 0 }
)




fun GymDayEntity.toDomainNew(
    blocks: List<TrainingBlockNew> = emptyList()
): GymDayNew = GymDayNew(
    name = this.day_name,
    description = this.description ?: "",
    position = this.position ?: -1,
    trainingBlocks = blocks
)
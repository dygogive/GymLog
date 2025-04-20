package com.example.gymlog.data.local.room.entity.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymlog.data.local.room.entity.plan.GymDayEntity
import com.example.gymlog.data.local.room.entity.plan.PlanCycleEntity

@Entity(
    tableName = "WorkoutGymDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanCycleEntity::class,
            parentColumns = ["id"],
            childColumns = ["plansID"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = GymDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["gymDaysID"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [                      // ← ДОДАЙ ОЦЕ
        Index(value = ["plansID"]),
        Index(value = ["gymDaysID"])
    ]
)
data class WorkoutGymDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val datetime: Long,
    val plansID: Long?,
    val gymDaysID: Long?,
    val sets: Int,
    val blocks: Int,
    val minutes: Int?,
    val name: String,
    val description: String?,
    val physicalСondition: Int?,
    val comments: String?
)
/**
 *
 *     val id: Long?, //ід
 *     val datetime: Long, //дата й час
 *     val plansId: Long?, // ід плану (програми) тренувань
 *     val gymDaysId: Long?, // ід заготовки тренування, що  у програмі
 *     val sets: Int, // кількість підходів за це тренування
 *     val blocks: Int, // к-сть виконаних тренувальних блоків (WorkoutGym)
 *     val minutes: Int?, //тривалість тренування у хв
 *     val name: String, // назва тренування (збігається з шаблоном тренування (GymSession)
 *     val description: String?, //опис тренування (збігається з шаблоном тренування (GymSession)
 *     val physicalСondition: Int?, //суб'єктивна оцінка фізичних кондицій при виконанні тренування 1...5
 *     val comments: String? //записані коментарі
 */
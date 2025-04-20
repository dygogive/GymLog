package com.example.gymlog.data.local.room.entity.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymlog.data.local.room.entity.plan.TrainingBlockEntity

// WorkoutSet.kt
@Entity(
    tableName = "WorkoutSet",
    foreignKeys = [
        ForeignKey(
            entity = TrainingBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["tr_block_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = WorkoutGymDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [                     // ← ДОДАЙ
        Index(value = ["workout_id"]),
        Index(value = ["tr_block_id"])
    ]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val workout_id: Long?,
    val tr_block_id: Long?,
    val name: String,
    val description: String?,
    val position: Int,
    val physicalСondition: Int?, //суб'єктивна оцінка фізичних кондицій при виконанні блоку 1...5
    val comments: String? //записані коментарі
)

/**
val id: Long?, // ід в базі
val workoutId: Long?, //ід виконаного тренування
val trainingBlockId: Long?, //ід тренувального блоку, що є шаблоном цього
val name: String, //назва - збігається з назвоб шаблону трен блоку
val description: String?,//опис - збігається з назвоб шаблону трен блоку
val position: Int, //позиція в тренуванні - збігається з позицією в шаблоні трен блоку
val physicalСondition: Int?, //суб'єктивна оцінка фізичних кондицій при виконанні блоку 1...5
val comments: String? //записані коментарі
 */
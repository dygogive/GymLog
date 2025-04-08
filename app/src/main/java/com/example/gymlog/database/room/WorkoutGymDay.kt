package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "WorkoutGymDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanCycles::class, //Ентіті з батьківської таблиці
            parentColumns = ["id"], //батьківський ід
            childColumns = ["plansID"], //дочірній ід
            onDelete = ForeignKey.SET_NULL //якщо видалити батьківський рядок в PlanCycles то planCycleID = null
        ),
        ForeignKey(
            entity = GymDays::class, //Ентіті з батьківської таблиці
            parentColumns = ["id"], //батьківський ід
            childColumns = ["gymDaysID"], //дочірній ід
            onDelete = ForeignKey.SET_NULL //якщо видалити батьківський рядок в PlanCycles то planCycleID = null
        )
    ]
)

data class WorkoutGymDay(
    @PrimaryKey(autoGenerate = true)
    val id:             Long = 0,   //id
    val datetime:       Long,       //дата й час
    val plansID:        Long?,      //ссилка на таблицю PlanCycles
    val gymDaysID:      Long?,      //ссилка на таблицю GymDays
    val sets:           Int,        // к-сть сетів
    val blocks:         Int,        // к-сть виконаних блоків
    val minutes:        Int,        // к-сть хвилин тренування
    val name:           String,     // Назва програми
    val description:    String      // Опис програми
)
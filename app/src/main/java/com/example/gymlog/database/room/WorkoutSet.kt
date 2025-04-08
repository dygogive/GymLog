package com.example.gymlog.database.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "WorkoutSet",
    foreignKeys = [
        ForeignKey(
            entity =            WorkoutGymDay::class,   //Ентіті з батьківської таблиці
            parentColumns =     ["id"],                 //батьківський ід
            childColumns =      ["workout_id"],         //дочірній ід
            onDelete =          ForeignKey.CASCADE      //якщо видалити батьківський рядок в PlanCycles то planCycleID = null
        ),
        ForeignKey(
            entity =            GymDays::class,         //Ентіті з батьківської таблиці
            parentColumns =     ["id"],                 //батьківський ід
            childColumns =      ["gymDays_id"],         //дочірній ід
            onDelete =          ForeignKey.SET_NULL     //якщо видалити батьківський рядок в PlanCycles то planCycleID = null
        )
    ]
)

data class WorkoutSet(
    @PrimaryKey(autoGenerate = true)
    val id:           Long = 0,
    val workout_id:   Int,
    val gymDays_id:   Int,
    val day_name:     String,
    val description:  String,
    val position:     Int
)
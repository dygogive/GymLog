package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gymlog.data.local.room.dto.ExerciseInBlockDto

@Dao
interface ExerciseInBlockDao {



    @Query("SELECT " +
            "tbe.id AS linkId, " +
            "e.id AS exerciseId, " +
            "e.name, e.description, e.motion, e.muscleGroups, e.equipment, e.isCustom, tbe.position " +
            "FROM TrainingBlockExercises tbe " +
            "JOIN Exercise e " +
            "ON e.id = tbe.exerciseId " +
            "WHERE tbe.trainingBlockId = :blockID " +
            "ORDER BY tbe.position ASC")
    suspend fun getExercisesForBlock(blockID: Long?): List<ExerciseInBlockDto>



}
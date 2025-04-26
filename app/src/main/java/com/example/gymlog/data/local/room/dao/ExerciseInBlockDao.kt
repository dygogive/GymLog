package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gymlog.data.local.room.dto.ExerciseInBlockDto
import com.example.gymlog.domain.model.exercise.ExerciseInBlock

@Dao
interface ExerciseInBlockDao {



    @Query("SELECT " +
            "tbe.id AS linkId, " +
            "e.id AS exerciseId, " +
            "e.name, e.description, e.motioState, e.muscleGroups, e.equipmentState, e.isCustom, tbe.position " +
            "FROM TrainingBlockExercises tbe " +
            "JOIN Exercise e " +
            "ON e.id = tbe.exerciseId " +
            "WHERE tbe.trainingBlockId = :blockID " +
            "ORDER BY tbe.position ASC")
    suspend fun getExercisesForBlock(blockID: Long): List<ExerciseInBlockDto>



}
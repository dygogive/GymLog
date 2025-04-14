package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.gymlog.domain.model.exercise.ExerciseInBlock

@Dao
interface ExerciseInBlockDao {
    @Insert
    suspend fun insert(exerciseInBlock: ExerciseInBlock): Long

    @Update
    suspend fun update(exerciseInBlock: ExerciseInBlock): Int

    @Delete
    suspend fun delete(exerciseInBlock: ExerciseInBlock): Int

}
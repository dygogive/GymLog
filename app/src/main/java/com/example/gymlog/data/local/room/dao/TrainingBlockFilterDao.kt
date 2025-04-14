package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gymlog.domain.model.exercise.Equipment
import com.example.gymlog.domain.model.exercise.Motion
import com.example.gymlog.domain.model.exercise.MuscleGroup
import com.example.gymlog.domain.model.plan.BlockFilters
import com.example.gymlog.utils.parseEnumOrNull

@Dao
interface TrainingBlockFilterDao {

    //Motion
    @Query("SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId = :blockId")
    suspend fun getMotions(blockId: Long): List<String>

    //MuscleGroups
    @Query("SELECT muscleGroup FROM TrainingBlockMuscleGroup WHERE trainingBlockId = :blockId")
    suspend fun getMuscles(blockId: Long): List<String>

    //Equipment
    @Query("SELECT equipment FROM TrainingBlockEquipment WHERE trainingBlockId = :blockId")
    suspend fun getEquipments(blockId: Long): List<String>

    //Motion + MuscleGroups + Equipment
    suspend fun getAllFiltersForBlock(blockId: Long): BlockFilters {
        val motions = getMotions(blockId).mapNotNull { parseEnumOrNull<Motion>(it) }
        val muscles = getMuscles(blockId).mapNotNull { parseEnumOrNull<MuscleGroup>(it) }
        val equipment = getEquipments(blockId).mapNotNull { parseEnumOrNull<Equipment>(it) }

        return BlockFilters(
            motions,
            muscles,
            equipment
        )
    }
}
package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.plan.BlockFilters
import com.example.gymlog.core.utils.parseEnumOrNull

@Dao
interface TrainingBlockFilterDao {

    //MotioStateList
    @Query("SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId = :blockId")
    suspend fun getMotions(blockId: Long): List<String>

    //MuscleGroups
    @Query("SELECT muscleGroup FROM TrainingBlockMuscleGroup WHERE trainingBlockId = :blockId")
    suspend fun getMuscles(blockId: Long): List<String>

    //EquipmentStateList
    @Query("SELECT equipment FROM TrainingBlockEquipment WHERE trainingBlockId = :blockId")
    suspend fun getEquipments(blockId: Long): List<String>

    //MotioStateList + MuscleGroups + EquipmentStateList
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
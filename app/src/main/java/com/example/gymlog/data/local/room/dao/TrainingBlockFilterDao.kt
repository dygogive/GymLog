package com.example.gymlog.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.legacy.plan.BlockFilters
import com.example.gymlog.core.utils.parseEnumOrNull
import com.example.gymlog.data.local.room.dto.BlockFiltersDto

@Dao
interface TrainingBlockFilterDao {

    //Motion
    @Query("SELECT motionType FROM TrainingBlockMotion WHERE trainingBlockId = :blockId")
    suspend fun getMotions(blockId: Long?): List<String>

    //MuscleGroups
    @Query("SELECT muscleGroup FROM TrainingBlockMuscleGroup WHERE trainingBlockId = :blockId")
    suspend fun getMuscles(blockId: Long?): List<String>

    //Equipment
    @Query("SELECT equipment FROM TrainingBlockEquipment WHERE trainingBlockId = :blockId")
    suspend fun getEquipments(blockId: Long?): List<String>

    //Motion + MuscleGroups + Equipment
    suspend fun getAllFiltersForBlock(blockId: Long?): BlockFiltersDto {
        val motions = getMotions(blockId)
        val muscles = getMuscles(blockId)
        val equipment = getEquipments(blockId)

        return BlockFiltersDto(
            motions,
            muscles,
            equipment
        )
    }
}
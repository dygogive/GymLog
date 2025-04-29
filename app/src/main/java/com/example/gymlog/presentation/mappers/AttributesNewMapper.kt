package com.example.gymlog.presentation.mappers

import com.example.gymlog.domain.model.attributenew.EquipmentNew
import com.example.gymlog.domain.model.attributenew.MotionNew
import com.example.gymlog.domain.model.attributenew.MuscleGroupNew

/**
 * Загальний mapper для перетворення різних типів атрибутів вправ у ресурси рядків
 */
object AttributesNewMapper {

    /**
     * Перетворює обладнання у ресурс рядка
     * @param equipment тип обладнання
     * @return ID ресурсу рядка
     */
    fun mapEquipmentToStringResource(equipment: EquipmentNew) =
        EquipmentNewMapper.mapToStringResource(equipment)

    /**
     * Перетворює тип руху у ресурс рядка
     * @param motion тип руху
     * @return ID ресурсу рядка
     */
    fun mapMotionToStringResource(motion: MotionNew) =
        MotionNewMapper.mapToStringResource(motion)

    /**
     * Перетворює групу м'язів у ресурс рядка
     * @param muscleGroup група м'язів
     * @return ID ресурсу рядка
     */
    fun mapMuscleGroupToStringResource(muscleGroup: MuscleGroupNew) =
        MuscleGroupNewMapper.mapToStringResource(muscleGroup)
}
package com.example.gymlog.presentation.mappers

import com.example.gymlog.R
import com.example.gymlog.domain.model.attributenew.MotionNew

/**
 * Mapper для перетворення MotionNew enum у відповідний ресурс рядка
 */
object MotionNewMapper {

    /**
     * Повертає ID ресурсу рядка для вказаного типу руху
     * @param motion тип руху
     * @return ID ресурсу рядка
     */
    fun mapToStringResource(motion: MotionNew): Int {
        return when (motion) {
            MotionNew.PRESS_UPWARDS -> R.string.motion_press_upwards
            MotionNew.PRESS_DOWNWARDS -> R.string.motion_press_downwards
            MotionNew.PRESS_MIDDLE -> R.string.motion_press_middle
            MotionNew.PRESS_UP_MIDDLE -> R.string.motion_press_up_middle
            MotionNew.PRESS_DOWN_MIDDLE -> R.string.motion_press_down_middle
            MotionNew.PULL_UPWARDS -> R.string.motion_pull_upwards
            MotionNew.PULL_DOWNWARDS -> R.string.motion_pull_downwards
            MotionNew.PULL_MIDDLE -> R.string.motion_pull_middle
            MotionNew.PULL_UP_MIDDLE -> R.string.motion_pull_up_middle
            MotionNew.PULL_DOWN_MIDDLE -> R.string.motion_pull_down_middle
            MotionNew.PRESS_BY_LEGS -> R.string.motion_press_by_legs
            MotionNew.PULL_BY_LEGS -> R.string.motion_pull_by_legs
        }
    }
}
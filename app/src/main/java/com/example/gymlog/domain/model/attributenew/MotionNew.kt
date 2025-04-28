package com.example.gymlog.domain.model.attributenew

import androidx.annotation.StringRes
import com.example.gymlog.R

enum class MotionNew(@StringRes val resId: Int) {
    PRESS_UPWARDS(R.string.motion_press_upwards),
    PRESS_DOWNWARDS(R.string.motion_press_downwards),
    PRESS_MIDDLE(R.string.motion_press_middle),
    PRESS_UP_MIDDLE(R.string.motion_press_up_middle),
    PRESS_DOWN_MIDDLE(R.string.motion_press_down_middle),
    PULL_UPWARDS(R.string.motion_pull_upwards),
    PULL_DOWNWARDS(R.string.motion_pull_downwards),
    PULL_MIDDLE(R.string.motion_pull_middle),
    PULL_UP_MIDDLE(R.string.motion_pull_up_middle),
    PULL_DOWN_MIDDLE(R.string.motion_pull_down_middle),
    PRESS_BY_LEGS(R.string.motion_press_by_legs),
    PULL_BY_LEGS(R.string.motion_pull_by_legs)
}
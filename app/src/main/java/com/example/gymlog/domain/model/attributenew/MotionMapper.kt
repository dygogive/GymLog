package com.example.gymlog.domain.model.attributenew

import com.example.gymlog.domain.model.attribute.motion.Motion

object MotionMapper {

    fun fromString(value: String?): Motion? {
        return try {
            value?.let { Motion.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun toString(motion: Motion?): String? {
        return motion?.name
    }
}
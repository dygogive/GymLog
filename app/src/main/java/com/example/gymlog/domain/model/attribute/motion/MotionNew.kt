package com.example.gymlog.domain.model.attribute.motion

sealed interface MotionNew {
    val name:           String
    val description:    String

    data class PressUpwards(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PressDownwards(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PressMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PressUpMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PressDownMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullUpwards(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullDownwards(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullUpMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullDownMiddle(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PressByLegs(
        override val name:          String,
        override val description:   String
    ) : MotionNew

    data class PullByLegs(
        override val name:          String,
        override val description:   String
    ) : MotionNew
}

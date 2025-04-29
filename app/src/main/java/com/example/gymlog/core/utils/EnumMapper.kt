package com.example.gymlog.core.utils

object EnumMapper {

    inline fun <reified T : Enum<T>> fromString(value: String, default: T): T {
        return try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            default
        }
    }

    fun <T : Enum<T>> toString(enumValue: T): String {
        return enumValue.name
    }

    inline fun <reified T : Enum<T>> fromStringList(input: String, default: T): List<T> {
        return input.split(",")
            .mapNotNull { raw ->
                val cleaned = raw.trim()
                try {
                    enumValueOf<T>(cleaned)
                } catch (e: IllegalArgumentException) {
                    null // або default, якщо хочеш завжди заповнювати
                }
            }
    }

    fun <T : Enum<T>> toStringList(enumList: List<T>): String {
        return enumList.joinToString(",") { it.name }
    }
}
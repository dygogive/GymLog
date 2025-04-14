package com.example.gymlog.utils



inline fun <reified T : Enum<T>> parseEnumOrNull(str: String?): T? {
    return try {
        if (str.isNullOrBlank()) null else enumValueOf<T>(str.trim())
    } catch (e: Exception) {
        null
    }
}


inline fun <reified T : Enum<T>> parseEnumList(commaSeparated: String?): List<T> {
    if (commaSeparated.isNullOrBlank()) return emptyList()

    return commaSeparated.split(",")
        .mapNotNull { parseEnumOrNull<T>(it) }
}

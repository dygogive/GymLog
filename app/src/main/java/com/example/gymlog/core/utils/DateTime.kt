package com.example.gymlog.core.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

//дає пару з датою й часом
fun getCurrentDateTime(): Pair<String, String> {
    val currentDateTime = LocalDateTime.now()

    // Форматуємо дату у вигляді "день.місяць.рік" (00.00.0000)
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formattedDate = currentDateTime.format(dateFormatter)

    // Форматуємо час у вигляді "години:хвилини" (00:00)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formattedTime = currentDateTime.format(timeFormatter)

    return Pair(formattedDate, formattedTime)
}


fun getCurrentDateTimeLegacy(): Pair<String, String> {
    val calendar = Calendar.getInstance()

    // Форматуємо дату (00.00.0000)
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(calendar.time)

    // Форматуємо час (00:00)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(calendar.time)

    return Pair(formattedDate, formattedTime)
}




fun formatTime(ms: Long, includeHours: Boolean = true): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (includeHours && hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

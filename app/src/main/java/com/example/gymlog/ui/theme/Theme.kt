package com.example.gymlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.gymlog.R

@Composable
fun MyAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Отримуємо кольори з ресурсів
    val backgroundActivity = colorResource(id = R.color.background_activity)
    val backgroundCard = colorResource(id = R.color.background_card)
    val textPrimary = colorResource(id = R.color.text_primary)
    val textSecondary = colorResource(id = R.color.text_secondary)
    val textHint = colorResource(id = R.color.text_hint)
    val buttonPrimary = colorResource(id = R.color.button_primary)
    val buttonSecondary = colorResource(id = R.color.button_secondary)
    val buttonTextPrimary = colorResource(id = R.color.button_text_primary)
    val buttonTextSecondary = colorResource(id = R.color.button_text_secondary)
    val accentColor = colorResource(id = R.color.accent_color)

    // Створюємо схему для світлої теми
    val LightColorScheme = lightColorScheme(
        primary = buttonPrimary,
        onPrimary = buttonTextPrimary,
        primaryContainer = backgroundCard,
        onPrimaryContainer = textPrimary,
        secondary = buttonSecondary,
        onSecondary = buttonTextSecondary,
        secondaryContainer = backgroundCard,
        onSecondaryContainer = textSecondary,
        tertiary = accentColor,
        onTertiary = buttonTextPrimary,
        background = backgroundActivity,
        onBackground = textPrimary,
        surface = backgroundCard,
        onSurface = textPrimary,
        surfaceVariant = textHint,
        error = Color(0xFFB00020),
        onError = Color.White
    )

    // Android автоматично підставить відповідні значення з res/values‑night
    val DarkColorScheme = darkColorScheme(
        primary = buttonPrimary, // у темній темі ці значення визначені у відповідних ресурсах
        onPrimary = buttonTextPrimary,
        primaryContainer = backgroundCard,
        onPrimaryContainer = textPrimary,
        secondary = buttonSecondary,
        onSecondary = buttonTextSecondary,
        secondaryContainer = backgroundCard,
        onSecondaryContainer = textSecondary,
        tertiary = accentColor,
        onTertiary = buttonTextPrimary,
        background = backgroundActivity,
        onBackground = textPrimary,
        surface = backgroundCard,
        onSurface = textPrimary,
        surfaceVariant = textHint,
        error = Color(0xFFCF6679),
        onError = Color.Black
    )

    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

package com.example.gymlog.feature.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
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

    //Кастомний шрифт
    val CustomFontFamily = FontFamily(
        Font(R.font.roboto_regular, FontWeight.Normal),
        // Якщо треба інші варіанти (Bold, Italic), додаєте ще
        Font(R.font.roboto_bold, FontWeight.Bold),
        Font(R.font.roboto_italic, FontWeight.Normal)
    )


    // Визначте типографіку
    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = CustomFontFamily, //застосування кастомного шрифту
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        // Додайте інші текстові стилі за необхідності
        bodyLarge = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.25.sp
        ),
        // Спеціальні стилі
        labelLarge = TextStyle(
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        )
    )



    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography, // Додаємо нашу типографіку
        content = content
    )
}

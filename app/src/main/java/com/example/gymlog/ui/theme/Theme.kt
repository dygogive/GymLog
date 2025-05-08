package com.example.gymlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.gymlog.R

// Кольори для світлої теми (60-30-10 принцип)
// 60% - нейтральні кольори (фон, картки)
// 30% - основні кольори взаємодії (кнопки, заголовки)
// 10% - акцентні кольори (виділення, фокус)

// Колір для світлої теми
private val Light60 = Color(0xFFF8F9FA) // Основний фон
private val Light30 = Color(0xFF4361EE) // Основний колір кнопок
private val Light10 = Color(0xFF7209B7) // Акцентний колір

// Колір для темної теми
private val Dark60 = Color(0xFF121418) // Основний фон
private val Dark30 = Color(0xFF4CC9F0) // Основний колір кнопок
private val Dark10 = Color(0xFFF72585) // Акцентний колір

// Custom font family
private val CustomFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_italic, FontWeight.Normal)
)

// App typography
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = CustomFontFamily,
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
    bodyLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,       // Зменшено для компактності
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
        fontSize = 12.sp,      // Збільшено для кращої читабельності
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,      // Зменшено для компактності
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun MyAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) {
        darkColorScheme(
            // 60% - основні кольори
            background = colorResource(id = R.color.background_activity),
            onBackground = colorResource(id = R.color.text_primary),
            surface = colorResource(id = R.color.background_card),
            onSurface = colorResource(id = R.color.text_primary),
            surfaceVariant = colorResource(id = R.color.background_card2),
            onSurfaceVariant = colorResource(id = R.color.text_secondary),

            // 30% - кольори взаємодії
            primary = colorResource(id = R.color.button_primary),
            onPrimary = colorResource(id = R.color.button_text_primary),
            primaryContainer = colorResource(id = R.color.button_secondary),
            onPrimaryContainer = colorResource(id = R.color.button_text_secondary),
            secondary = colorResource(id = R.color.button_secondary),
            onSecondary = colorResource(id = R.color.button_text_secondary),

            // 10% - акцентні кольори
            tertiary = colorResource(id = R.color.accent_color),
            onTertiary = colorResource(id = R.color.button_text_primary),
            error = colorResource(id = R.color.error_color),
            onError = Color.White
        )
    } else {
        lightColorScheme(
            // 60% - основні кольори
            background = colorResource(id = R.color.background_activity),
            onBackground = colorResource(id = R.color.text_primary),
            surface = colorResource(id = R.color.background_card),
            onSurface = colorResource(id = R.color.text_primary),
            surfaceVariant = colorResource(id = R.color.background_card2),
            onSurfaceVariant = colorResource(id = R.color.text_secondary),

            // 30% - кольори взаємодії
            primary = colorResource(id = R.color.button_primary),
            onPrimary = colorResource(id = R.color.button_text_primary),
            primaryContainer = colorResource(id = R.color.button_secondary),
            onPrimaryContainer = colorResource(id = R.color.button_text_secondary),
            secondary = colorResource(id = R.color.button_secondary),
            onSecondary = colorResource(id = R.color.button_text_secondary),

            // 10% - акцентні кольори
            tertiary = colorResource(id = R.color.accent_color),
            onTertiary = colorResource(id = R.color.button_text_primary),
            error = colorResource(id = R.color.error_color),
            onError = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
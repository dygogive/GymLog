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
        fontSize = 20.sp,
        lineHeight = 28.sp,
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
    labelLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
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
            primary = colorResource(id = R.color.button_primary),
            onPrimary = colorResource(id = R.color.button_text_primary),
            primaryContainer = colorResource(id = R.color.background_card),
            onPrimaryContainer = colorResource(id = R.color.text_primary),
            secondary = colorResource(id = R.color.button_secondary),
            onSecondary = colorResource(id = R.color.button_text_secondary),
            secondaryContainer = colorResource(id = R.color.background_card),
            onSecondaryContainer = colorResource(id = R.color.text_secondary),
            tertiary = colorResource(id = R.color.accent_color),
            onTertiary = colorResource(id = R.color.button_text_primary),
            background = colorResource(id = R.color.background_activity),
            onBackground = colorResource(id = R.color.text_primary),
            surface = colorResource(id = R.color.background_card),
            onSurface = colorResource(id = R.color.text_primary),
            surfaceVariant = colorResource(id = R.color.text_hint),
            error = Color(0xFFCF6679),
            onError = Color.Black
        )
    } else {
        lightColorScheme(
            primary = colorResource(id = R.color.button_primary),
            onPrimary = colorResource(id = R.color.button_text_primary),
            primaryContainer = colorResource(id = R.color.background_card),
            onPrimaryContainer = colorResource(id = R.color.text_primary),
            secondary = colorResource(id = R.color.button_secondary),
            onSecondary = colorResource(id = R.color.button_text_secondary),
            secondaryContainer = colorResource(id = R.color.background_card),
            onSecondaryContainer = colorResource(id = R.color.text_secondary),
            tertiary = colorResource(id = R.color.accent_color),
            onTertiary = colorResource(id = R.color.button_text_primary),
            background = colorResource(id = R.color.background_activity),
            onBackground = colorResource(id = R.color.text_primary),
            surface = colorResource(id = R.color.background_card),
            onSurface = colorResource(id = R.color.text_primary),
            surfaceVariant = colorResource(id = R.color.text_hint),
            error = Color(0xFFB00020),
            onError = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

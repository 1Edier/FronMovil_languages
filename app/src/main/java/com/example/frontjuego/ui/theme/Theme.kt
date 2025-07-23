package com.example.frontjuego.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta de colores personalizada para la app de idiomas
private val LinguaQuestColorScheme = lightColorScheme(
    primary = Color(0xFF667eea),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF667eea).copy(alpha = 0.1f),
    onPrimaryContainer = Color(0xFF667eea),
    secondary = Color(0xFF764ba2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF764ba2).copy(alpha = 0.1f),
    onSecondaryContainer = Color(0xFF764ba2),
    tertiary = Color(0xFFFFD700),
    onTertiary = Color(0xFF2D3748),
    tertiaryContainer = Color(0xFFFFD700).copy(alpha = 0.1f),
    onTertiaryContainer = Color(0xFFFFD700),
    error = Color(0xFFFF6B6B),
    onError = Color.White,
    errorContainer = Color(0xFFFF6B6B).copy(alpha = 0.1f),
    onErrorContainer = Color(0xFFFF6B6B),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF2D3748),
    surface = Color.White,
    onSurface = Color(0xFF2D3748),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),
    scrim = Color(0xFF000000).copy(alpha = 0.5f),
    inverseSurface = Color(0xFF2D3748),
    inverseOnSurface = Color.White,
    inversePrimary = Color(0xFF667eea).copy(alpha = 0.8f),
    surfaceDim = Color(0xFFF7FAFC),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E0)
)

// Tipografía personalizada
private val LinguaQuestTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Tema principal de la aplicación
@Composable
fun LinguaQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LinguaQuestColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LinguaQuestTypography,
        content = content
    )
}

// Colores adicionales para los mundos
object WorldColors {
    val Forest = Color(0xFF4CAF50)
    val ForestLight = Color(0xFF81C784)
    val ForestDark = Color(0xFF2E7D32)

    val Ocean = Color(0xFF2196F3)
    val OceanLight = Color(0xFF64B5F6)
    val OceanDark = Color(0xFF1565C0)

    val Desert = Color(0xFFFFC107)
    val DesertLight = Color(0xFFFFD54F)
    val DesertDark = Color(0xFFF57F17)

    val Mountain = Color(0xFF795548)
    val MountainLight = Color(0xFFA1887F)
    val MountainDark = Color(0xFF5D4037)

    val City = Color(0xFF9C27B0)
    val CityLight = Color(0xFFBA68C8)
    val CityDark = Color(0xFF7B1FA2)
}
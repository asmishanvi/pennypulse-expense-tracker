package com.example.expensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    background = LightBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    accent: ThemeAccent = ThemeAccent.Mint,
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = baseScheme.copy(
        primary = accent.primary,
        onPrimary = accent.onPrimary,
        secondary = accent.secondary,
        onSecondary = accent.onSecondary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ExpenseTrackerTypography,
        shapes = AppShapes,
        content = content
    )
}

@Composable
fun backgroundBrush(height: Dp, darkTheme: Boolean, accent: ThemeAccent): Brush {
    val colors = if (darkTheme) {
        listOf(
            DarkBackground,
            lerp(DarkBackground, accent.primary, 0.35f),
            lerp(DarkBackground, accent.secondary, 0.25f),
            lerp(DarkBackground, DarkSurface, 0.65f)
        )
    } else {
        listOf(
            LightBackground,
            lerp(LightBackground, accent.primary, 0.22f),
            lerp(LightBackground, accent.secondary, 0.18f),
            lerp(LightBackground, LightSurfaceVariant, 0.5f)
        )
    }
    return Brush.verticalGradient(
        colors = colors,
        startY = 0f,
        endY = with(LocalDensity.current) { height.toPx() }
    )
}

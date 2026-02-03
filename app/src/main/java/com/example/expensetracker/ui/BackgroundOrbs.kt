package com.example.expensetracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.expensetracker.ui.theme.ThemeAccent

@Composable
fun BackgroundOrbs(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    accent: ThemeAccent
) {
    val colors = if (darkTheme) {
        listOf(accent.primary, accent.secondary, accent.primary)
    } else {
        listOf(accent.primary, accent.secondary, accent.primary)
    }

    Canvas(modifier = modifier) {
        val minDim = size.minDimension
        drawCircle(
            color = colors[0].copy(alpha = 0.22f),
            radius = minDim * 0.55f,
            center = Offset(size.width * 0.9f, size.height * 0.1f)
        )
        drawCircle(
            color = colors[1].copy(alpha = 0.18f),
            radius = minDim * 0.45f,
            center = Offset(size.width * 0.1f, size.height * 0.32f)
        )
        drawCircle(
            color = colors[2].copy(alpha = 0.16f),
            radius = minDim * 0.5f,
            center = Offset(size.width * 0.85f, size.height * 0.88f)
        )
        drawCircle(
            color = colors[1].copy(alpha = 0.12f),
            radius = minDim * 0.28f,
            center = Offset(size.width * 0.2f, size.height * 0.8f)
        )
    }
}

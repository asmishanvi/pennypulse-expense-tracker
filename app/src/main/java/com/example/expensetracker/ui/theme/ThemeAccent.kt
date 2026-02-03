package com.example.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeAccent(
    val label: String,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color
) {
    Mint(
        label = "Mint",
        primary = AccentMint,
        onPrimary = Color(0xFF0F1113),
        secondary = AccentCoral,
        onSecondary = Color(0xFF0F1113)
    ),
    Coral(
        label = "Coral",
        primary = AccentCoral,
        onPrimary = Color(0xFF0F1113),
        secondary = AccentMint,
        onSecondary = Color(0xFF0F1113)
    ),
    Blue(
        label = "Blue",
        primary = AccentBlue,
        onPrimary = Color(0xFF0F1113),
        secondary = AccentYellow,
        onSecondary = Color(0xFF0F1113)
    ),
    Yellow(
        label = "Yellow",
        primary = AccentYellow,
        onPrimary = Color(0xFF0F1113),
        secondary = AccentBlue,
        onSecondary = Color(0xFF0F1113)
    )
}

@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.example.expensetracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.expensetracker.R

private val SoraFont = GoogleFont("Sora")
private val SoraProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val SoraFamily = FontFamily(
    Font(googleFont = SoraFont, fontProvider = SoraProvider, weight = FontWeight.Normal),
    Font(googleFont = SoraFont, fontProvider = SoraProvider, weight = FontWeight.Medium),
    Font(googleFont = SoraFont, fontProvider = SoraProvider, weight = FontWeight.SemiBold),
    Font(googleFont = SoraFont, fontProvider = SoraProvider, weight = FontWeight.Bold),
    Font(googleFont = SoraFont, fontProvider = SoraProvider, weight = FontWeight.ExtraBold)
)

val ExpenseTrackerTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        letterSpacing = 0.2.sp,
        lineHeight = 38.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SoraFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp
    )
)

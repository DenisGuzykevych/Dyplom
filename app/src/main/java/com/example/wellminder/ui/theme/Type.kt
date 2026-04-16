package com.example.wellminder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.wellminder.R

// Набір стилів типографіки Material за замовчуванням
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Comfortaa")

val ComfortaaFontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

val defaultColor = Color(0xFF1C1B1F)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ComfortaaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = defaultColor
    ),
    displayLarge = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    displayMedium = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    displaySmall = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    headlineLarge = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    headlineMedium = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    headlineSmall = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    titleLarge = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    titleMedium = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    titleSmall = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    bodyMedium = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    bodySmall = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    labelLarge = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    labelMedium = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
    labelSmall = TextStyle(fontFamily = ComfortaaFontFamily, color = defaultColor),
)
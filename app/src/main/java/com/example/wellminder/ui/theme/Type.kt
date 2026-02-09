package com.example.wellminder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.wellminder.R

// Set of Material typography styles to start with
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Comfortaa")

val ComfortaaFontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ComfortaaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(fontFamily = ComfortaaFontFamily),
    displayMedium = TextStyle(fontFamily = ComfortaaFontFamily),
    displaySmall = TextStyle(fontFamily = ComfortaaFontFamily),
    headlineLarge = TextStyle(fontFamily = ComfortaaFontFamily),
    headlineMedium = TextStyle(fontFamily = ComfortaaFontFamily),
    headlineSmall = TextStyle(fontFamily = ComfortaaFontFamily),
    titleLarge = TextStyle(fontFamily = ComfortaaFontFamily),
    titleMedium = TextStyle(fontFamily = ComfortaaFontFamily),
    titleSmall = TextStyle(fontFamily = ComfortaaFontFamily),
    bodyMedium = TextStyle(fontFamily = ComfortaaFontFamily),
    bodySmall = TextStyle(fontFamily = ComfortaaFontFamily),
    labelLarge = TextStyle(fontFamily = ComfortaaFontFamily),
    labelMedium = TextStyle(fontFamily = ComfortaaFontFamily),
    labelSmall = TextStyle(fontFamily = ComfortaaFontFamily),
)
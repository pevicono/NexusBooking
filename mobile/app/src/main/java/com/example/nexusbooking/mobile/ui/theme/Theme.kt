package com.example.nexusbooking.mobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NexusBluePrimary,
    secondary = NexusBlueLight,
    tertiary = NexusSuccess,
    background = Color(0xFF0D1F3D),
    surface = Color(0xFF132A4F),
    surfaceVariant = Color(0xFF1B365F),
    primaryContainer = Color(0xFF1F3E68),
    outline = Color(0xFF35557F),
    onPrimary = NexusWhite,
    onSecondary = NexusWhite,
    onBackground = Color(0xFFE5ECF7),
    onSurface = Color(0xFFE5ECF7),
    onSurfaceVariant = Color(0xFFD6E0EF),
    error = NexusError
)

private val LightColorScheme = lightColorScheme(
    primary = NexusBluePrimary,
    secondary = NexusBlueLight,
    tertiary = NexusSuccess,
    background = NexusGreyLight,
    surface = NexusWhite,
    surfaceVariant = NexusSurfaceVariant,
    primaryContainer = NexusPrimaryContainer,
    outline = NexusDivider,
    onPrimary = NexusWhite,
    onSecondary = NexusWhite,
    onBackground = NexusBlueDark,
    onSurface = NexusBlueDark,
    onSurfaceVariant = NexusTextSecondary,
    error = NexusError
)

@Composable
fun NexusBookingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = NexusShapes,
        content = content
    )
}
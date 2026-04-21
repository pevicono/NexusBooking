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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NexusBlueLight,
    secondary = NexusBlue,
    tertiary = NexusSuccess
)

private val LightColorScheme = lightColorScheme(
    primary = NexusBlue,
    secondary = NexusBlueLight,
    tertiary = NexusSuccess,
    background = NexusSurface,
    surface = NexusCard,
    onPrimary = NexusCard,
    onSecondary = NexusCard,
    onBackground = NexusText,
    onSurface = NexusText,
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
        content = content
    )
}
package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = lightColorScheme(
    primary = GeoPrimary,
    onPrimary = GeoWhite,
    primaryContainer = GeoPrimaryContainer,
    onPrimaryContainer = GeoOnPrimaryContainer,
    secondary = GeoSecondary,
    onSecondary = GeoOnSecondary,
    secondaryContainer = GeoPillBg,
    onSecondaryContainer = GeoTextSecondary,
    tertiary = GeoRed,
    onTertiary = GeoWhite,
    background = GeoBg,
    onBackground = GeoTextPrimary,
    surface = GeoWhite,
    onSurface = GeoTextPrimary,
    surfaceVariant = GeoPillBg,
    onSurfaceVariant = GeoTextSecondary,
    error = GeoRed,
    onError = GeoWhite,
    outline = GeoBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Force false to align with Geometric Balance light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TitaniumPrimary,
    onPrimary = TitaniumOnPrimary,
    primaryContainer = SlateSurfaceVariant,
    onPrimaryContainer = TitaniumPrimary,
    secondary = SlateSecondary,
    onSecondary = Color.White,
    secondaryContainer = SlateSurfaceContainer,
    onSecondaryContainer = TitaniumPrimary,
    tertiary = NordicCyan,
    onTertiary = SlateBackground,
    background = SlateBackground,
    onBackground = TitaniumPrimary,
    surface = SlateSurface,
    onSurface = TitaniumPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateSecondary,
    outline = SlateBorder,
    outlineVariant = SlateBorderLight,
    surfaceContainer = SlateSurfaceContainer
)

private val LightColorScheme = lightColorScheme(
    primary = LightTextPrimary,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightTextPrimary,
    secondary = LightTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = LightPorcelainBackground,
    onBackground = LightTextPrimary,
    surface = LightChalkSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    surfaceContainer = LightSurfaceVariant
)

@Composable
fun AddonVaultTheme(
    darkTheme: Boolean = true, // Default to sleek matte dark theme per user request
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

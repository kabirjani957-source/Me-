package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PrismDarkColorScheme =
  darkColorScheme(
    primary = CyanElectric,
    onPrimary = OnCyan,
    primaryContainer = StudioSurfaceElevated,
    onPrimaryContainer = CyanGlow,
    secondary = AmberPrism,
    onSecondary = OnAmber,
    secondaryContainer = StudioSurfaceElevated,
    onSecondaryContainer = AmberPrism,
    tertiary = VioletShader,
    background = ObsidianVoid,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    error = ErrorCoral,
  )

private val PrismShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun Prism3DTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = PrismDarkColorScheme,
    typography = Typography,
    shapes = PrismShapes,
    content = content,
  )
}

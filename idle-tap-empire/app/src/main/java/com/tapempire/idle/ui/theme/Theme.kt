package com.tapempire.idle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TapEmpireColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepPurple,
    secondary = Diamond,
    onSecondary = DeepPurple,
    background = DeepPurple,
    onBackground = TextPrimary,
    surface = MidPurple,
    onSurface = TextPrimary,
    surfaceVariant = CardPurple,
    onSurfaceVariant = TextSecondary
)

@Composable
fun TapEmpireTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TapEmpireColorScheme,
        typography = TapEmpireTypography,
        content = content
    )
}

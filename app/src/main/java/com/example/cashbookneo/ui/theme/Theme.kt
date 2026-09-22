package com.example.cashbookneo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = MidnightOnPrimary,
    primaryContainer = MidnightPrimaryContainer,
    onPrimaryContainer = MidnightOnPrimaryContainer,
    inversePrimary = MidnightInversePrimary,
    secondary = MidnightSecondary,
    onSecondary = MidnightOnSecondary,
    secondaryContainer = MidnightSecondaryContainer,
    onSecondaryContainer = MidnightOnSecondaryContainer,
    tertiary = MidnightTertiary,
    onTertiary = MidnightOnTertiary,
    tertiaryContainer = MidnightTertiaryContainer,
    onTertiaryContainer = MidnightOnTertiaryContainer,
    background = MidnightBackground,
    onBackground = MidnightOnBackground,
    surface = MidnightSurface,
    onSurface = MidnightOnSurface,
    surfaceVariant = MidnightSurfaceContainerHighest,
    onSurfaceVariant = MidnightOnSurfaceVariant,
    outline = MidnightOutline,
    outlineVariant = MidnightOutlineVariant,
    surfaceTint = MidnightSurfaceTint,
    inverseSurface = MidnightInverseSurface,
    inverseOnSurface = MidnightInverseOnSurface,
    error = MidnightError,
    onError = MidnightOnError,
    errorContainer = MidnightErrorContainer,
    onErrorContainer = MidnightOnErrorContainer
)

// The design is focused on dark mode, but we can provide a fallback or similar light scheme if needed.
// For now, we'll keep the dark-first executive aesthetic.
private val LightColorScheme = lightColorScheme(
    primary = MidnightPrimary,
    onPrimary = MidnightOnPrimary,
    // ... potentially other mappings for light mode if required later
)

@Composable
fun CashFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default to maintain brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We'll use the DarkColorScheme for both to ensure the "Midnight Ledger" look
    // unless the user specifically wants a light mode (which isn't detailed in the design yet)
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

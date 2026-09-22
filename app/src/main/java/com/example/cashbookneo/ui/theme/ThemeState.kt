package com.example.cashbookneo.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ThemeState(initialDarkMode: Boolean = true) {
    var isDarkMode by mutableStateOf(initialDarkMode)
}

val LocalTheme = compositionLocalOf<ThemeState> {
    error("No ThemeState provided")
}

package com.onthecrow.nomadrates.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.onthecrow.nomadrates.util.theme.ThemeMode

object AppThemeProvider {
    fun resolveDarkTheme(
        themeMode: ThemeMode,
        isSystemDarkTheme: Boolean,
    ): Boolean {
        return when (themeMode) {
            ThemeMode.SYSTEM -> isSystemDarkTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    }

    @Composable
    fun rememberDarkTheme(themeMode: ThemeMode): Boolean {
        return resolveDarkTheme(
            themeMode = themeMode,
            isSystemDarkTheme = isSystemInDarkTheme(),
        )
    }
}

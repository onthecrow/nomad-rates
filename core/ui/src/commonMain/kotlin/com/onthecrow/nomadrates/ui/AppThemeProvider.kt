package com.onthecrow.nomadrates.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

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

    /**
     * Whether the app theme is dark, or `null` until [themeModes] has delivered the stored mode.
     *
     * Draw nothing while it is `null`. Assuming the system theme until then would show the app in
     * the wrong theme for the first frames after launch and then switch it without the reveal.
     */
    @Composable
    fun rememberDarkTheme(themeModes: Flow<ThemeMode>): Boolean? {
        val themeMode by themeModes.collectAsState(initial = null)
        return themeMode?.let { rememberDarkTheme(it) }
    }
}

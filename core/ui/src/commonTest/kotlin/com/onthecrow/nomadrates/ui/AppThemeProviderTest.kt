package com.onthecrow.nomadrates.ui

import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppThemeProviderTest {

    @Test
    fun `system mode mirrors system theme`() {
        assertTrue(AppThemeProvider.resolveDarkTheme(ThemeMode.SYSTEM, isSystemDarkTheme = true))
        assertFalse(AppThemeProvider.resolveDarkTheme(ThemeMode.SYSTEM, isSystemDarkTheme = false))
    }

    @Test
    fun `light mode is always light`() {
        assertFalse(AppThemeProvider.resolveDarkTheme(ThemeMode.LIGHT, isSystemDarkTheme = true))
        assertFalse(AppThemeProvider.resolveDarkTheme(ThemeMode.LIGHT, isSystemDarkTheme = false))
    }

    @Test
    fun `dark mode is always dark`() {
        assertTrue(AppThemeProvider.resolveDarkTheme(ThemeMode.DARK, isSystemDarkTheme = true))
        assertTrue(AppThemeProvider.resolveDarkTheme(ThemeMode.DARK, isSystemDarkTheme = false))
    }
}

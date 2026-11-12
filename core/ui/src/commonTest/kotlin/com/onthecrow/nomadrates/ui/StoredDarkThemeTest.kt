package com.onthecrow.nomadrates.ui

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The app is not drawn in a guessed theme before the stored one is read: that theme would then be
 * replaced without the reveal right after launch.
 */
@OptIn(ExperimentalTestApi::class)
class StoredDarkThemeTest {

    @Test
    fun `theme is unknown until the stored mode is read`() = runComposeUiTest {
        val themeModes = MutableSharedFlow<ThemeMode>(replay = 1)
        val darkThemes = recordDarkThemes(themeModes)

        mainClock.advanceTimeBy(1_000)
        waitForIdle()

        assertEquals(listOf<Boolean?>(null), darkThemes)
    }

    // The system theme of the test is either light or dark, so assuming it before the stored mode
    // is read fails one of the next two tests.

    @Test
    fun `stored light theme is the first theme applied`() = runComposeUiTest {
        val themeModes = MutableSharedFlow<ThemeMode>(replay = 1)
        val darkThemes = recordDarkThemes(themeModes)

        themeModes.tryEmit(ThemeMode.LIGHT)
        waitForIdle()

        assertEquals(listOf(null, false), darkThemes)
    }

    @Test
    fun `stored dark theme is the first theme applied`() = runComposeUiTest {
        val themeModes = MutableSharedFlow<ThemeMode>(replay = 1)
        val darkThemes = recordDarkThemes(themeModes)

        themeModes.tryEmit(ThemeMode.DARK)
        waitForIdle()

        assertEquals(listOf(null, true), darkThemes)
    }

    @Test
    fun `follows changes of the stored mode`() = runComposeUiTest {
        val themeModes = MutableSharedFlow<ThemeMode>(replay = 1)
        val darkThemes = recordDarkThemes(themeModes)

        themeModes.tryEmit(ThemeMode.LIGHT)
        waitForIdle()
        themeModes.tryEmit(ThemeMode.DARK)
        waitForIdle()

        assertEquals(listOf(null, false, true), darkThemes)
    }

    /** Every distinct theme the content was composed with, in order. */
    private fun ComposeUiTest.recordDarkThemes(themeModes: Flow<ThemeMode>): List<Boolean?> {
        val darkThemes = mutableListOf<Boolean?>()
        setContent {
            val darkTheme = AppThemeProvider.rememberDarkTheme(themeModes)
            SideEffect {
                if (darkThemes.isEmpty() || darkThemes.last() != darkTheme) darkThemes += darkTheme
            }
        }
        return darkThemes
    }
}

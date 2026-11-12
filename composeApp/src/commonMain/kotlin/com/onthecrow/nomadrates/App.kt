package com.onthecrow.nomadrates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.onthecrow.nomadrates.navigation.NavigationProvider
import com.onthecrow.nomadrates.settings.domain.ObserveThemeModeUseCase
import com.onthecrow.nomadrates.ui.AppThemeProvider
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.ThemeRevealHost
import com.onthecrow.nomadrates.ui.rememberThemeRevealState
import com.onthecrow.nomadrates.ui.view.SystemBarShadeView
import org.koin.compose.getKoin

/**
 * @param onThemeLoaded called once the stored theme is known, right before the app is first drawn
 * in it. Until then nothing is drawn.
 */
@Composable
@Preview
fun App(onThemeLoaded: () -> Unit = {}) {
    val koin = getKoin()
    val themeModes = remember(koin) { koin.get<ObserveThemeModeUseCase>().invoke() }
    val darkTheme = AppThemeProvider.rememberDarkTheme(themeModes) ?: return
    SideEffect { onThemeLoaded() }

    val themeReveal = rememberThemeRevealState()
    // System bar icons keep the old theme until the reveal has uncovered the whole screen.
    AppSystemBarsEffect(themeReveal.outgoingDarkTheme ?: darkTheme)

    NomadRatesTheme(
        dynamicColor = false,
        darkTheme = darkTheme,
    ) {
        val navigationProvider = getKoin().get<NavigationProvider>()

        ThemeRevealHost(
            modifier = Modifier.fillMaxSize(),
            darkTheme = darkTheme,
            state = themeReveal,
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                navigationProvider.Navigation(modifier = Modifier.fillMaxSize())
                SystemBarShadeView(
                    modifier = Modifier.fillMaxWidth(),
                    position = com.onthecrow.nomadrates.ui.view.ShadePosition.TOP,
                )
                SystemBarShadeView(
                    modifier = Modifier.fillMaxWidth(),
                    position = com.onthecrow.nomadrates.ui.view.ShadePosition.BOTTOM,
                )
            }
        }
    }
}

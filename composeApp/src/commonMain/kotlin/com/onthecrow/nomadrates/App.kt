package com.onthecrow.nomadrates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.onthecrow.nomadrates.navigation.NavigationProvider
import com.onthecrow.nomadrates.settings.domain.ObserveThemeModeUseCase
import com.onthecrow.nomadrates.ui.AppThemeProvider
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.SystemBarShadeView
import com.onthecrow.nomadrates.util.theme.ThemeMode
import org.koin.compose.getKoin

@Composable
@Preview
fun App() {
    val themeMode by getKoin()
        .get<ObserveThemeModeUseCase>()
        .invoke()
        .collectAsState(initial = ThemeMode.SYSTEM)
    val darkTheme = AppThemeProvider.rememberDarkTheme(themeMode)

    NomadRatesTheme(
        dynamicColor = false,
        darkTheme = darkTheme,
    ) {
        val navigationProvider = getKoin().get<NavigationProvider>()

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

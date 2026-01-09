package com.onthecrow.nomadrates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.onthecrow.nomadrates.navigation.NavigationProvider
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.SystemBarShadeView
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin

@Composable
@Preview
fun App() {
    NomadRatesTheme(
        // TODO take it from settings later
        dynamicColor = false,
        darkTheme = true,
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

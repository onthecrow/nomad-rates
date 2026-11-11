package com.onthecrow.nomadrates.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider

@androidx.compose.runtime.Composable
actual fun NomadRatesTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @androidx.compose.runtime.Composable (() -> Unit)
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(LocalNomadRatesSemanticColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

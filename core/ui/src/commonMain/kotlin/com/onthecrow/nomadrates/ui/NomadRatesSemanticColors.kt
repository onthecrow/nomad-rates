package com.onthecrow.nomadrates.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
internal data class NomadRatesSemanticColors(
    val positiveAccent: Color,
    val negativeAccent: Color,
    val favouriteAccent: Color,
    val highlightOverlay: Color,
)

internal val LightSemanticColors = NomadRatesSemanticColors(
    positiveAccent = Color(0xFF4E7A57),
    negativeAccent = MutedClay,
    favouriteAccent = MutedClay,
    highlightOverlay = md_theme_light_primary.copy(alpha = 0.08f),
)

internal val DarkSemanticColors = NomadRatesSemanticColors(
    positiveAccent = PaleEmerald,
    negativeAccent = MutedClay,
    favouriteAccent = StrongPastelRed,
    highlightOverlay = Color.White.copy(alpha = 0.1f),
)

internal val LocalNomadRatesSemanticColors = staticCompositionLocalOf {
    DarkSemanticColors
}

val ColorScheme.positiveAccent: Color
    @Composable get() = LocalNomadRatesSemanticColors.current.positiveAccent

val ColorScheme.negativeAccent: Color
    @Composable get() = LocalNomadRatesSemanticColors.current.negativeAccent

val ColorScheme.favouriteAccent: Color
    @Composable get() = LocalNomadRatesSemanticColors.current.favouriteAccent

val ColorScheme.highlightOverlay: Color
    @Composable get() = LocalNomadRatesSemanticColors.current.highlightOverlay

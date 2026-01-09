package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

enum class ShadePosition {
    TOP, BOTTOM;
}

private data class SystemBarShadeViewState(
    val alignment: Alignment,
    val height: Dp,
    val colors: List<Color>,
)

@Composable
fun BoxScope.SystemBarShadeView(
    position: ShadePosition,
    modifier: Modifier = Modifier,
) {
    val shadeColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
    val systemBarsPaddings = WindowInsets.systemBars.asPaddingValues()
    val state = remember(position, systemBarsPaddings, shadeColor) {
        when (position) {
            ShadePosition.TOP -> SystemBarShadeViewState(
                alignment = Alignment.TopCenter,
                height = systemBarsPaddings.calculateTopPadding() * 2,
                colors = listOf(shadeColor, Color.Transparent),
            )
            ShadePosition.BOTTOM -> SystemBarShadeViewState(
                alignment = Alignment.BottomCenter,
                height = systemBarsPaddings.calculateBottomPadding() * 2,
                colors = listOf(Color.Transparent, shadeColor),
            )
        }
    }

    Box(
        modifier = modifier
            .height(state.height)
            .align(state.alignment)
            .background(brush = Brush.verticalGradient(colors = state.colors)),
    )
}

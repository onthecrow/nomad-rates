package com.onthecrow.nomadrates.ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp

fun Modifier.shimmer(
    shape: Shape,
    baseColor: Color = Color.Unspecified,
    highlightColor: Color = Color.Unspecified,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1_100,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-progress",
    )

    val resolvedBaseColor = if (baseColor != Color.Unspecified) {
        baseColor
    } else {
        lerp(
            start = MaterialTheme.colorScheme.secondaryContainer,
            stop = MaterialTheme.colorScheme.onSurface,
            fraction = 0.14f,
        )
    }
    val resolvedHighlightColor = if (highlightColor != Color.Unspecified) {
        highlightColor
    } else {
        lerp(
            start = MaterialTheme.colorScheme.secondaryContainer,
            stop = MaterialTheme.colorScheme.onSurface,
            fraction = 0.28f,
        )
    }

    this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                resolvedBaseColor,
                resolvedHighlightColor,
                resolvedBaseColor,
            ),
            start = Offset(x = progress.value * 700f - 350f, y = 0f),
            end = Offset(x = progress.value * 700f, y = 250f),
        ),
        shape = shape,
    )
}

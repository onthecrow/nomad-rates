package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.NomadRatesTheme

private const val DEFAULT_ANIMATION_DURATION = 1500

@Composable
fun CurrencyChart(
    data: List<Double>,
    graphColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    animationDuration: Int = DEFAULT_ANIMATION_DURATION,
) {
    if (data.size < 2) return

    var animationProgress by remember(data) { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(data, graphColor) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        )
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val verticalPadding = strokeWidth.toPx() / 2
        val drawableHeight = height - strokeWidth.toPx()

        val maxVal = data.maxOrNull() ?: 0.0
        val minVal = data.minOrNull() ?: 0.0

        val isFlat = maxVal == minVal

        val range = if (isFlat) 1.0 else maxVal - minVal

        val stepX = width / (data.size - 1)

        val points = data.mapIndexed { index, value ->
            val normalizedFraction = if (isFlat) {
                0.5
            } else {
                (value - minVal) / range
            }

            val y = verticalPadding + (1f - normalizedFraction.toFloat()) * drawableHeight
            Offset(x = index * stepX, y = y)
        }

        val strokePath = Path()
        val fillPath = Path()

        if (points.isNotEmpty()) {
            strokePath.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val current = points[i]
                val next = points[i + 1]

                val deltaX = (next.x - current.x) / 2f
                val controlPoint1 = Offset(current.x + deltaX, current.y)
                val controlPoint2 = Offset(next.x - deltaX, next.y)

                strokePath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    next.x, next.y
                )
                fillPath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    next.x, next.y
                )
            }
        }

        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        clipRect(right = width * animationProgress.value) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(graphColor.copy(alpha = 0.4f), Color.Transparent),
                    startY = 0f,
                    endY = height,
                ),
            )

            drawPath(
                path = strokePath,
                color = graphColor,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun CurrencyChartPreview() {
    NomadRatesTheme {
        val sampleData = listOf(
            917f, 915.381333f, 915.381333f, 915.442733f, 915.381333f,
            917f, 917f, 917f, 915.381333f, 915.381333f,
            915.4288f, 912.2861f, 912.2861f, 912.2861f, 912.2861f,
            912.2861f, 915.4287f, 915.428667f, 912.286f, 912.286f,
            917f, 917f, 914.643f, 915.428667f, 915.02f,
            915.428667f, 915.428667f, 917f, 917f, 915.4287f
        ).map { value -> value.toDouble() }

        CurrencyChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            data = sampleData,
            graphColor = Color(0xFF2962FF),
            strokeWidth = 4.dp,
        )
    }
}

package com.onthecrow.nomadrates.ui.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import kotlin.math.roundToInt

private const val ROLL_DURATION_MILLIS = 220

/**
 * Text for numbers whose characters roll like an odometer when they change: a digit that grows
 * comes in from below, one that gets smaller comes in from above. Characters are matched from the
 * end, so digits keep their places: when a number gets longer, the new leading digits roll in from
 * below, and when it gets shorter, the extra ones roll out downwards while the text narrows.
 *
 * The text is one line, drawn from its right edge: when it is wider than the space it gets, its
 * start is cut off.
 *
 * All characters are drawn by one layout node and the animation only re-lays out and redraws it,
 * so long lists of these stay cheap.
 */
@Composable
fun RollingText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val glyphs = remember(textMeasurer, style) { GlyphCache(textMeasurer, style) }
    val lastText = remember { LastText(text) }
    // A new change starts from the text the previous change was going to.
    val roll = remember(text) {
        Roll(from = lastText.value, to = text).also { lastText.value = text }
    }
    val progress = remember(roll) { Animatable(if (roll.from == roll.to) 1f else 0f) }
    LaunchedEffect(progress) {
        progress.animateTo(1f, tween(ROLL_DURATION_MILLIS, easing = FastOutSlowInEasing))
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .semantics { contentDescription = text }
            .drawBehind { drawRoll(roll, glyphs, color, progress.value) },
    ) { _, constraints ->
        val fromWidth = glyphs.widthOf(roll.from)
        val toWidth = glyphs.widthOf(roll.to)
        val width = lerp(fromWidth, toWidth, progress.value).roundToInt()
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = glyphs.lineHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height, mapOf(FirstBaseline to glyphs.baseline)) {}
    }
}

private class LastText(var value: String)

private class Roll(val from: String, val to: String)

private class GlyphCache(
    private val textMeasurer: TextMeasurer,
    private val style: TextStyle,
) {
    private val glyphs = mutableMapOf<Char, TextLayoutResult>()
    private val sample = glyph('0')

    val lineHeight: Int = sample.size.height
    val baseline: Int = sample.firstBaseline.roundToInt()

    fun glyph(char: Char): TextLayoutResult = glyphs.getOrPut(char) {
        textMeasurer.measure(text = char.toString(), style = style, maxLines = 1, softWrap = false)
    }

    fun widthOf(text: String): Float = text.sumOf { char -> glyph(char).size.width }.toFloat()
}

/**
 * Draws [roll] at [progress] (0 is [Roll.from], 1 is [Roll.to]), from the right edge leftwards.
 * Characters at the same place from the end move between their old and new positions; when they
 * differ, the old one rolls out and the new one rolls in.
 */
private fun DrawScope.drawRoll(roll: Roll, glyphs: GlyphCache, color: Color, progress: Float) {
    val from = roll.from
    val to = roll.to
    val lineHeight = glyphs.lineHeight.toFloat()
    var fromRight = size.width
    var toRight = size.width

    for (placeFromEnd in 1..maxOf(from.length, to.length)) {
        val fromChar = from.getOrNull(from.length - placeFromEnd)
        val toChar = to.getOrNull(to.length - placeFromEnd)
        val fromGlyph = fromChar?.let(glyphs::glyph)
        val toGlyph = toChar?.let(glyphs::glyph)
        val fromLeft = fromRight - (fromGlyph?.size?.width ?: 0)
        val toLeft = toRight - (toGlyph?.size?.width ?: 0)

        when {
            fromGlyph != null && toGlyph != null && fromChar == toChar -> {
                drawGlyph(toGlyph, color, x = lerp(fromLeft, toLeft, progress), y = 0f, alpha = 1f)
            }

            else -> {
                // Up when a digit grows or a new one appears; down when it gets smaller or goes away.
                val direction = when {
                    fromChar == null -> 1f
                    toChar == null -> -1f
                    fromChar.isDigit() && toChar.isDigit() && toChar < fromChar -> -1f
                    else -> 1f
                }
                val x = when {
                    fromGlyph == null -> toLeft
                    toGlyph == null -> fromLeft
                    else -> lerp(fromLeft, toLeft, progress)
                }
                if (fromGlyph != null) {
                    drawGlyph(fromGlyph, color, x = x, y = -direction * progress * lineHeight, alpha = 1f - progress)
                }
                if (toGlyph != null) {
                    drawGlyph(toGlyph, color, x = x, y = direction * (1f - progress) * lineHeight, alpha = progress)
                }
            }
        }
        fromRight = fromLeft
        toRight = toLeft
    }
}

private fun DrawScope.drawGlyph(glyph: TextLayoutResult, color: Color, x: Float, y: Float, alpha: Float) {
    if (alpha <= 0f) return
    drawText(textLayoutResult = glyph, color = color, topLeft = Offset(x, y), alpha = alpha)
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun RollingTextPreview() {
    NomadRatesTheme(darkTheme = true) {
        RollingText(
            text = "1 234,56",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

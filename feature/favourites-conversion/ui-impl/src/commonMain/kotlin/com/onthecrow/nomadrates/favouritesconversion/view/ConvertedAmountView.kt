package com.onthecrow.nomadrates.favouritesconversion.view

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import com.onthecrow.nomadrates.favouritesconversion.model.CompactAmount
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.positiveAccent
import com.onthecrow.nomadrates.ui.view.RollingText
import kotlin.math.roundToInt

/**
 * Part of a list row the converted amount may take; the rest stays for the currency name.
 */
private const val MAX_WIDTH_FRACTION = 0.6f

private const val EXPONENT_FONT_SIZE_EM = 0.7f

/**
 * How far the exponent is raised above the baseline, as a fraction of its own font size. The
 * standard superscript (0.5) leaves it too low next to bold digits; this puts its top at the top
 * of the digits.
 */
private const val EXPONENT_RAISE = 0.9f

/**
 * A converted amount at the end of a list row; its digits roll when it changes. When [amount] is
 * too wide for the row, the [compactAmount] is shown instead ("2,16 × 10²¹").
 *
 * The choice is made while laying out, not with BoxWithConstraints: that one subcomposes, which is
 * too slow for every row of a list that changes on each typed digit.
 */
@Composable
internal fun ConvertedAmountView(
    amount: String,
    compactAmount: CompactAmount?,
    modifier: Modifier = Modifier,
) {
    val style: TextStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    val color = MaterialTheme.colorScheme.positiveAccent

    Layout(
        modifier = modifier.clipHorizontally(),
        content = {
            RollingText(
                text = amount,
                style = style,
                color = color,
            )
            if (compactAmount != null) {
                CompactAmountView(
                    compactAmount = compactAmount,
                    style = style,
                    color = color,
                )
            }
        },
    ) { measurables, constraints ->
        val maxAmountWidth = if (constraints.hasBoundedWidth) {
            (constraints.maxWidth * MAX_WIDTH_FRACTION).roundToInt()
        } else {
            Constraints.Infinity
        }
        val full = measurables[0].measure(Constraints())
        val shown = if (full.width <= maxAmountWidth || measurables.size < 2) {
            full
        } else {
            measurables[1].measure(Constraints(maxWidth = maxAmountWidth))
        }
        val width = minOf(shown.width, maxAmountWidth).coerceAtLeast(constraints.minWidth)
        layout(width, shown.height) {
            // Aligned to the end; what does not fit is cut off at the start.
            shown.placeRelative(x = width - shown.width, y = 0)
        }
    }
}

/**
 * Cuts off what is drawn past the start or the end of the view, but not above or below it: the
 * raised exponent of a compact amount goes above the view's top.
 */
private fun Modifier.clipHorizontally(): Modifier = drawWithContent {
    clipRect(top = -size.height, bottom = size.height * 2) {
        this@drawWithContent.drawContent()
    }
}

/**
 * "2,16 × 10²¹". The exponent is a separate text that is only drawn higher, which does not change
 * the height of the view, so the amount sits on the same line as plain amounts.
 */
@Composable
private fun CompactAmountView(
    compactAmount: CompactAmount,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val exponentStyle = style.copy(fontSize = style.fontSize * EXPONENT_FONT_SIZE_EM)
    val exponentRaisePx = with(LocalDensity.current) { (exponentStyle.fontSize * EXPONENT_RAISE).toPx() }

    Row(modifier = modifier) {
        RollingText(
            modifier = Modifier
                .weight(1f, fill = false)
                .alignByBaseline(),
            text = "${compactAmount.mantissa} × 10",
            style = style,
            color = color,
        )
        RollingText(
            modifier = Modifier
                .alignByBaseline()
                // Moved in drawing only: a layout offset would be undone by the baseline alignment.
                .graphicsLayer { translationY = -exponentRaisePx },
            text = compactAmount.exponent.toString(),
            style = exponentStyle,
            color = color,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConvertedAmountViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConvertedAmountView(
            amount = "2 160 864 034 639 659 177 530,00",
            compactAmount = CompactAmount(mantissa = "2,16", exponent = 21),
        )
    }
}

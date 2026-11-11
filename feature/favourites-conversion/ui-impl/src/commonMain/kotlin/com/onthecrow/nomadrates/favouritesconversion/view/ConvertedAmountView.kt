package com.onthecrow.nomadrates.favouritesconversion.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.onthecrow.nomadrates.favouritesconversion.model.CompactAmount
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.positiveAccent

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
 * A converted amount at the end of a list row. When [amount] is too wide for the row, the
 * [compactAmount] is shown instead ("2,16 × 10²¹"), and if even that does not fit, it is ellipsized.
 */
@Composable
internal fun ConvertedAmountView(
    amount: String,
    compactAmount: CompactAmount?,
    modifier: Modifier = Modifier,
) {
    val style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    val color = MaterialTheme.colorScheme.positiveAccent
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val maxAmountWidth = (constraints.maxWidth * MAX_WIDTH_FRACTION).toInt()
        val showCompact = remember(amount, compactAmount, style, maxAmountWidth, textMeasurer) {
            compactAmount != null && textMeasurer.measure(
                text = AnnotatedString(amount),
                style = style,
                maxLines = 1,
                softWrap = false,
            ).size.width > maxAmountWidth
        }
        val maxWidthModifier = Modifier.widthIn(max = with(density) { maxAmountWidth.toDp() })

        if (showCompact && compactAmount != null) {
            CompactAmountView(
                modifier = maxWidthModifier,
                compactAmount = compactAmount,
                style = style,
                color = color,
            )
        } else {
            Text(
                modifier = maxWidthModifier,
                text = amount,
                style = style,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
        Text(
            modifier = Modifier
                .weight(1f, fill = false)
                .alignByBaseline(),
            text = "${compactAmount.mantissa} × 10",
            style = style,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier
                .alignByBaseline()
                // Moved in drawing only: a layout offset would be undone by the baseline alignment.
                .graphicsLayer { translationY = -exponentRaisePx },
            text = compactAmount.exponent.toString(),
            style = exponentStyle,
            color = color,
            maxLines = 1,
            softWrap = false,
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

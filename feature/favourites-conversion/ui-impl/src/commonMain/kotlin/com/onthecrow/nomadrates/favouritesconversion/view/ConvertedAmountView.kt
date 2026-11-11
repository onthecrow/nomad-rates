package com.onthecrow.nomadrates.favouritesconversion.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import com.onthecrow.nomadrates.favouritesconversion.model.CompactAmount
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.positiveAccent

/**
 * Part of a list row the converted amount may take; the rest stays for the currency name.
 */
private const val MAX_WIDTH_FRACTION = 0.6f

private const val EXPONENT_FONT_SIZE_EM = 0.7f

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
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier) {
        val maxAmountWidth = (constraints.maxWidth * MAX_WIDTH_FRACTION).toInt()
        val text: AnnotatedString = remember(amount, compactAmount, style, maxAmountWidth, textMeasurer) {
            val fullText = AnnotatedString(amount)
            val fullWidth = textMeasurer.measure(
                text = fullText,
                style = style,
                maxLines = 1,
                softWrap = false,
            ).size.width
            if (compactAmount == null || fullWidth <= maxAmountWidth) fullText else compactAmount.toAnnotatedString()
        }
        Text(
            modifier = Modifier.widthIn(max = with(density) { maxAmountWidth.toDp() }),
            text = text,
            style = style,
            color = MaterialTheme.colorScheme.positiveAccent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun CompactAmount.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    append(mantissa)
    append(" × 10")
    withStyle(
        SpanStyle(
            baselineShift = BaselineShift.Superscript,
            fontSize = EXPONENT_FONT_SIZE_EM.em,
        )
    ) {
        append(exponent.toString())
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

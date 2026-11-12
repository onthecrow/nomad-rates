package com.onthecrow.nomadrates.ui.view

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val GROUP_SEPARATOR = ' '
private const val GROUP_SIZE = 3

/**
 * Formats the amount field: thousand groups are separated with spaces
 * ("1234567.8912" -> "1 234 567.8912").
 */
class CurrencyAmountInputVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val formatted = formatAmount(text.text)
        return TransformedText(AnnotatedString(formatted.text), formatted.offsetMapping)
    }
}

/**
 * The amount as the amount field shows it, with thousand groups ("1234567.89" -> "1 234 567.89").
 */
fun formatAmountForDisplay(amount: String): String = formatAmount(amount).text

private class FormattedAmount(
    val text: String,
    val offsetMapping: OffsetMapping,
)

private fun formatAmount(amount: String): FormattedAmount {
    val integerPartLength = amount.indexOf('.').let { index -> if (index >= 0) index else amount.length }
    // Index in the formatted text of every character of the amount, and its length at the end.
    val originalToTransformed = IntArray(amount.length + 1)
    val formatted = StringBuilder()

    amount.forEachIndexed { index, char ->
        val digitsLeftInIntegerPart = integerPartLength - index
        if (index in 1 until integerPartLength && digitsLeftInIntegerPart % GROUP_SIZE == 0) {
            formatted.append(GROUP_SEPARATOR)
        }
        originalToTransformed[index] = formatted.length
        formatted.append(char)
    }
    originalToTransformed[amount.length] = formatted.length

    // Number of characters of the amount before every position of the formatted text.
    val transformedToOriginal = IntArray(formatted.length + 1)
    var originalIndex = 0
    for (transformedIndex in 0..formatted.length) {
        while (originalIndex < amount.length && originalToTransformed[originalIndex] < transformedIndex) {
            originalIndex++
        }
        transformedToOriginal[transformedIndex] = originalIndex
    }

    return FormattedAmount(
        text = formatted.toString(),
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                originalToTransformed[offset.coerceIn(0, amount.length)]

            override fun transformedToOriginal(offset: Int): Int =
                transformedToOriginal[offset.coerceIn(0, formatted.length)]
        },
    )
}

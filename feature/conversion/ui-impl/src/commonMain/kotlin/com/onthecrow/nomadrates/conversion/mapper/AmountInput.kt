package com.onthecrow.nomadrates.conversion.mapper

/**
 * Keeps digits and the first decimal separator (`.` or `,`, normalised to `.`).
 */
internal fun String.filterAmountInput(): String {
    return buildString {
        var hasDot = false
        for (char in this@filterAmountInput) {
            if (char.isDigit()) {
                append(char)
            } else if (!hasDot && (char == '.' || char == ',')) {
                append('.')
                hasDot = true
            }
        }
    }
}

/**
 * Turns an amount shown in a field back into raw input like "1234.5". Unlike [filterAmountInput],
 * it also understands locale-formatted values such as "1,234.50" or "1 234,50": when both `.` and
 * `,` are present, the last one is the decimal separator, and a repeated one is a grouping separator.
 */
internal fun String.normalizeFormattedAmount(): String {
    val separators = filter { char -> char.isAmountSeparator() }
    val decimalSeparatorIndex = when {
        separators.isEmpty() -> -1
        separators.toSet().size > 1 -> indexOfLast { char -> char.isAmountSeparator() }
        separators.length == 1 -> indexOfFirst { char -> char.isAmountSeparator() }
        else -> -1
    }
    return buildString {
        this@normalizeFormattedAmount.forEachIndexed { index, char ->
            when {
                char.isDigit() -> append(char)
                index == decimalSeparatorIndex -> append('.')
            }
        }
    }
}

private fun Char.isAmountSeparator(): Boolean = this == '.' || this == ','

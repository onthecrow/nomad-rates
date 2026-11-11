package com.onthecrow.nomadrates.util

/**
 * Keeps digits and the first decimal separator (`.` or `,`, normalised to `.`).
 */
fun String.filterAmountInput(): String {
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

/** What an amount field shows when everything in it was deleted. */
const val EMPTY_AMOUNT_FIELD_VALUE: String = "0"

/**
 * The value of an amount field after an edit: [this] is the field's text after the edit, [previous]
 * is the text before it. The field is never empty and has no leading zeros:
 * - deleting everything leaves "0";
 * - typing into a field with only "0" replaces the zero, wherever the cursor is: "0" and "5" give
 *   "5" (not "05" or "50"), "0" and "0" stay "0", "0" and "." give "0.";
 * - leading zeros are dropped ("007" gives "7") and a missing one is added (".5" gives "0.5").
 * Otherwise it keeps digits and the first decimal separator, like [filterAmountInput].
 */
fun String.toAmountFieldValue(previous: String): String {
    var amount = filterAmountInput()
    if (previous == EMPTY_AMOUNT_FIELD_VALUE && amount.length == previous.length + 1) {
        // One character was typed next to the lone zero: it replaces the zero.
        val zeroIndex = amount.indexOf('0')
        if (zeroIndex >= 0) {
            amount = amount.removeRange(zeroIndex, zeroIndex + 1)
        }
    }
    val separatorIndex = amount.indexOf('.')
    val integerPart = (if (separatorIndex >= 0) amount.substring(0, separatorIndex) else amount)
        .trimStart('0')
        .ifEmpty { EMPTY_AMOUNT_FIELD_VALUE }
    return if (separatorIndex >= 0) integerPart + amount.substring(separatorIndex) else integerPart
}

/**
 * Turns an amount shown in a field back into raw input like "1234.5". Unlike [filterAmountInput],
 * it also understands locale-formatted values such as "1,234.50" or "1 234,50": when both `.` and
 * `,` are present, the last one is the decimal separator, and a repeated one is a grouping separator.
 */
fun String.normalizeFormattedAmount(): String {
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

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
 * An amount field after an edit: its text and where the selection (or the cursor) ends up.
 */
data class AmountFieldEdit(
    val text: String,
    val selectionStart: Int,
    val selectionEnd: Int,
)

/**
 * Applies the amount field rules to an edit and keeps the cursor where the user expects it.
 * [edited] is the field's text right after the edit with the selection [selectionStart] -
 * [selectionEnd]; [previous] is the text before the edit. The rules:
 * - only digits and the first decimal separator are kept (`,` becomes `.`);
 * - the field is never empty: deleting everything leaves "0";
 * - typing one character next to a lone "0" replaces the zero ("0" and "5" give "5", "0" and "0"
 *   stay "0", "0" and "." give "0.");
 * - leading zeros are dropped ("007" gives "7") and a missing one is added (".5" gives "0.5").
 * The selection moves by the characters removed or added before it, so it stays next to the same
 * characters.
 */
fun editAmountField(
    previous: String,
    edited: String,
    selectionStart: Int,
    selectionEnd: Int,
): AmountFieldEdit {
    // Where each position of the edited text ends up in the result.
    val positions = IntArray(edited.length + 1)
    val amount = StringBuilder()
    var hasSeparator = false
    edited.forEachIndexed { index, char ->
        positions[index] = amount.length
        if (char.isDigit()) {
            amount.append(char)
        } else if (!hasSeparator && (char == '.' || char == ',')) {
            amount.append('.')
            hasSeparator = true
        }
    }
    positions[edited.length] = amount.length

    fun movePositions(move: (Int) -> Int) {
        for (index in positions.indices) positions[index] = move(positions[index])
    }

    if (previous == EMPTY_AMOUNT_FIELD_VALUE && amount.length == previous.length + 1) {
        // One character was typed next to the lone zero: it replaces the zero.
        val zeroIndex = amount.indexOf('0')
        if (zeroIndex >= 0) {
            amount.deleteAt(zeroIndex)
            movePositions { position -> if (position > zeroIndex) position - 1 else position }
        }
    }

    val integerPartLength = amount.indexOf('.').let { index -> if (index >= 0) index else amount.length }
    val leadingZeros = amount.take(integerPartLength).takeWhile { char -> char == '0' }.length
    when {
        integerPartLength == 0 -> {
            // Nothing before the separator, or nothing at all: a zero goes there.
            amount.insert(0, EMPTY_AMOUNT_FIELD_VALUE)
            movePositions { position -> position + 1 }
        }

        leadingZeros > 0 -> {
            // Only zeros before the separator: one of them stays.
            val removed = if (leadingZeros == integerPartLength) leadingZeros - 1 else leadingZeros
            amount.deleteRange(0, removed)
            movePositions { position -> (position - removed).coerceAtLeast(0) }
        }
    }

    return AmountFieldEdit(
        text = amount.toString(),
        selectionStart = positions[selectionStart.coerceIn(0, edited.length)],
        selectionEnd = positions[selectionEnd.coerceIn(0, edited.length)],
    )
}

/**
 * An amount in the form the amount fields keep it: digits with at most one `.`, never empty and
 * without leading zeros ("007" gives "7", "" gives "0", ".5" gives "0.5").
 */
fun String.normalizeAmountInput(): String =
    editAmountField(previous = "", edited = this, selectionStart = 0, selectionEnd = 0).text

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

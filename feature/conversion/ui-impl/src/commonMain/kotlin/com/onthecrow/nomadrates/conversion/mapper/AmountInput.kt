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

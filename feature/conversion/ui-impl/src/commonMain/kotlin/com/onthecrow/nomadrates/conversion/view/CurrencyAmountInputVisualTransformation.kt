package com.onthecrow.nomadrates.conversion.view

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A VisualTransformation that formats a numeric string with thousand separators (spaces).
 * Example: "1234567.8912" -> "1 234 567.8912"
 *
 * Supports infinite decimal places and correct cursor positioning.
 */
class CurrencyAmountInputVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        // 1. Split the integer and decimal parts
        val dotIndex = originalText.indexOf('.')

        // If there is no dot, the whole string is the integer part
        val intPart = if (dotIndex == -1) originalText else originalText.substring(0, dotIndex)

        // The fraction part includes everything after the dot (unlimited length)
        val fractionPart = if (dotIndex == -1) "" else originalText.substring(dotIndex + 1)

        // 2. Format the integer part with spaces
        // We reverse the string, chunk it by 3, join with space, and reverse back.
        // Example: "12345" -> "54321" -> ["543", "21"] -> "543 21" -> "12 345"
        val formattedIntPart = if (intPart.isNotEmpty()) {
            intPart.reversed()
                .chunked(3)
                .joinToString(" ")
                .reversed()
        } else {
            ""
        }

        // 3. Construct the final display string
        val newText = buildString {
            append(formattedIntPart)
            if (dotIndex != -1) {
                append('.')
                append(fractionPart)
            }
        }

        // 4. Define the OffsetMapping to handle cursor behavior
        val offsetMapping = object : OffsetMapping {

            /**
             * Maps a position from the original string (raw data) to the formatted string.
             * Used when the cursor moves or text is added.
             */
            override fun originalToTransformed(offset: Int): Int {
                // Determine the cursor position relative to the decimal point
                val offsetInIntPart = if (dotIndex == -1) offset else offset.coerceAtMost(dotIndex)

                // Calculate how many spaces exist in the formatted integer part up to this offset.
                // Logic:
                // 1. Calculate the number of digits from the RIGHT side of the integer part.
                // 2. Determine how many groups of 3 exist.

                // However, an iterative approach is safer and less prone to "off-by-one" errors
                // than mathematical formulas when dealing with variable cursors.

                var formattedOffset = 0
                var originalIndex = 0

                // Iterate through the formatted string to find the matching position
                while (originalIndex < offsetInIntPart) {
                    // If the current char in formatted string is a space, it's an added char.
                    // We skip it (increment formattedOffset) but do NOT increment originalIndex.
                    if (formattedOffset < newText.length && newText[formattedOffset] == ' ') {
                        formattedOffset++
                    } else {
                        // Match found, advance both
                        formattedOffset++
                        originalIndex++
                    }
                }

                // Edge case: If the loop finishes and the very next character is a space,
                // we should move the cursor past it so it doesn't stay "before" a space.
                if (formattedOffset < newText.length && newText[formattedOffset] == ' ') {
                    formattedOffset++
                }

                // If the offset was inside or after the fraction part, add that difference directly
                if (offset > offsetInIntPart) {
                    val fractionOffset = offset - offsetInIntPart
                    formattedOffset += fractionOffset
                }

                return formattedOffset.coerceAtMost(newText.length)
            }

            /**
             * Maps a position from the formatted string back to the original string.
             * Used when the user clicks/touches a specific position.
             */
            override fun transformedToOriginal(offset: Int): Int {
                // Simply count non-space characters up to the given offset
                val textUpToCursor = newText.take(offset)
                val nonSpaceCount = textUpToCursor.count { it != ' ' }
                return nonSpaceCount.coerceAtMost(originalText.length)
            }
        }

        return TransformedText(AnnotatedString(newText), offsetMapping)
    }
}

package com.onthecrow.nomadrates.ui.view

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyAmountInputVisualTransformationTest {

    private val transformation = CurrencyAmountInputVisualTransformation()

    @Test
    fun `amount is shown in thousand groups`() {
        assertEquals("1 234 567.8912", formatAmountForDisplay("1234567.8912"))
        assertEquals("123", formatAmountForDisplay("123"))
        assertEquals("0.004000545", formatAmountForDisplay("0.004000545"))
        assertEquals("12.", formatAmountForDisplay("12."))
    }

    @Test
    fun `field text is the displayed amount`() {
        assertEquals("1 234.5", transformation.filter(AnnotatedString("1234.5")).text.text)
    }

    @Test
    fun `every cursor position maps there and back`() {
        listOf("1234567.8912", "0.004000545", "12.", "0").forEach { amount ->
            val mapping = transformation.filter(AnnotatedString(amount)).offsetMapping
            for (offset in 0..amount.length) {
                assertEquals(offset, mapping.transformedToOriginal(mapping.originalToTransformed(offset)), amount)
            }
        }
    }

    @Test
    fun `cursor next to a group space stays between the same digits`() {
        val transformed = transformation.filter(AnnotatedString("1234"))
        val text = transformed.text.text
        val spaceIndex = text.indexOf(' ')

        // Both sides of the space are between "1" and "2".
        assertEquals(1, transformed.offsetMapping.transformedToOriginal(spaceIndex))
        assertEquals(1, transformed.offsetMapping.transformedToOriginal(spaceIndex + 1))
    }
}

package com.onthecrow.nomadrates.conversion.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

class AmountInputTest {

    @Test
    fun `raw input stays as it is`() {
        assertEquals("1234.5", "1234.5".normalizeFormattedAmount())
        assertEquals("12.", "12.".normalizeFormattedAmount())
        assertEquals("0.0001", "0.0001".normalizeFormattedAmount())
        assertEquals("", "".normalizeFormattedAmount())
    }

    @Test
    fun `single comma is a decimal separator`() {
        assertEquals("92.00", "92,00".normalizeFormattedAmount())
    }

    @Test
    fun `grouping separators are dropped`() {
        assertEquals("1234.50", "1,234.50".normalizeFormattedAmount())
        assertEquals("1234.50", "1.234,50".normalizeFormattedAmount())
        assertEquals("1234.50", "1 234,50".normalizeFormattedAmount())
        assertEquals("1234.50", "1 234,50".normalizeFormattedAmount())
        assertEquals("1234567", "1,234,567".normalizeFormattedAmount())
    }
}

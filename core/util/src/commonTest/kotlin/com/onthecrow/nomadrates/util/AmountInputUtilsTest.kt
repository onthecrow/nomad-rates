package com.onthecrow.nomadrates.util

import kotlin.test.Test
import kotlin.test.assertEquals

class AmountInputUtilsTest {

    @Test
    fun `amount input keeps digits and the first separator`() {
        assertEquals("1.50", "1a,5.0".filterAmountInput())
        assertEquals("12.", "12.".filterAmountInput())
        assertEquals("", "abc".filterAmountInput())
    }

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

    @Test
    fun `deleting everything leaves zero`() {
        assertEquals("0", "".toAmountFieldValue(previous = "5"))
        assertEquals("0", "".toAmountFieldValue(previous = "0"))
    }

    @Test
    fun `typing next to a lone zero replaces it`() {
        assertEquals("5", "05".toAmountFieldValue(previous = "0"))
        assertEquals("5", "50".toAmountFieldValue(previous = "0"))
        assertEquals("0", "00".toAmountFieldValue(previous = "0"))
        assertEquals("0.", "0.".toAmountFieldValue(previous = "0"))
        assertEquals("0.", ".0".toAmountFieldValue(previous = "0"))
        assertEquals("0.", "0,".toAmountFieldValue(previous = "0"))
    }

    @Test
    fun `leading zeros are dropped and a missing one is added`() {
        assertEquals("7", "007".toAmountFieldValue(previous = "07"))
        assertEquals("0", "00".toAmountFieldValue(previous = "100"))
        assertEquals("5", "05".toAmountFieldValue(previous = "105"))
        assertEquals("0.5", ".5".toAmountFieldValue(previous = "0.5"))
        assertEquals("0.50", "000.50".toAmountFieldValue(previous = "1"))
    }

    @Test
    fun `ordinary edits are kept`() {
        assertEquals("10", "10".toAmountFieldValue(previous = "1"))
        assertEquals("100", "100".toAmountFieldValue(previous = "10"))
        assertEquals("12.", "12.".toAmountFieldValue(previous = "12"))
        assertEquals("1.05", "1.05".toAmountFieldValue(previous = "1.0"))
        assertEquals("1", "1a".toAmountFieldValue(previous = "1"))
    }
}

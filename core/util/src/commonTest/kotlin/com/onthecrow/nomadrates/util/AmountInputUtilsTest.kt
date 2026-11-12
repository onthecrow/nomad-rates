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
    fun `deleting everything leaves zero with the cursor after it`() {
        assertEquals(AmountFieldEdit("0", 1, 1), editAmountField(previous = "5", edited = "", 0, 0))
    }

    @Test
    fun `typing next to a lone zero replaces it`() {
        assertEquals(AmountFieldEdit("5", 1, 1), editAmountField(previous = "0", edited = "05", 2, 2))
        assertEquals(AmountFieldEdit("5", 1, 1), editAmountField(previous = "0", edited = "50", 1, 1))
        assertEquals(AmountFieldEdit("0", 1, 1), editAmountField(previous = "0", edited = "00", 2, 2))
        assertEquals(AmountFieldEdit("0.", 2, 2), editAmountField(previous = "0", edited = "0.", 2, 2))
        assertEquals(AmountFieldEdit("0.", 2, 2), editAmountField(previous = "0", edited = "0,", 2, 2))
    }

    @Test
    fun `typing before a leading zero keeps the cursor after the typed digit`() {
        // "0.004" with the cursor before "." and "3" typed.
        assertEquals(
            AmountFieldEdit("3.004", 1, 1),
            editAmountField(previous = "0.004", edited = "03.004", 2, 2),
        )
    }

    @Test
    fun `deleting the only integer digit leaves a zero with the cursor after it`() {
        // "3.004" with the cursor before "." and the "3" deleted.
        assertEquals(
            AmountFieldEdit("0.004", 1, 1),
            editAmountField(previous = "3.004", edited = ".004", 0, 0),
        )
    }

    @Test
    fun `edits in the middle keep the cursor in place`() {
        assertEquals(
            AmountFieldEdit("3.0074", 4, 4),
            editAmountField(previous = "3.004", edited = "3.0074", 4, 4),
        )
        assertEquals(
            AmountFieldEdit("1234", 2, 2),
            editAmountField(previous = "134", edited = "1234", 2, 2),
        )
        assertEquals(
            AmountFieldEdit("14", 1, 1),
            editAmountField(previous = "134", edited = "14", 1, 1),
        )
    }

    @Test
    fun `deleting the first digit drops the zeros after it`() {
        // "1005" with the "1" deleted: "005" becomes "5", the cursor stays at the start.
        assertEquals(AmountFieldEdit("5", 0, 0), editAmountField(previous = "1005", edited = "005", 0, 0))
        assertEquals(AmountFieldEdit("0", 0, 0), editAmountField(previous = "100", edited = "00", 0, 0))
    }

    @Test
    fun `selection moves with the text`() {
        assertEquals(
            AmountFieldEdit("12.5", 0, 4),
            editAmountField(previous = "12.5", edited = "12.5", 0, 4),
        )
    }

    @Test
    fun `amount input is normalized`() {
        assertEquals("7", "007".normalizeAmountInput())
        assertEquals("0", "".normalizeAmountInput())
        assertEquals("0", "00".normalizeAmountInput())
        assertEquals("0.5", ".5".normalizeAmountInput())
        assertEquals("0.50", "000,50".normalizeAmountInput())
        assertEquals("10", "10".normalizeAmountInput())
        assertEquals("12.", "12.".normalizeAmountInput())
        assertEquals("1", "1a".normalizeAmountInput())
    }
}

package com.onthecrow.nomadrates.util

import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyUtilsJvmTest {
    private val originalLocale = Locale.getDefault()

    @BeforeTest
    fun setUp() {
        Locale.setDefault(Locale.forLanguageTag("ru"))
    }

    @AfterTest
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `toCurrencyName ignores device locale when english name is requested`() {
        assertEquals("Russian Ruble", "RUB".toCurrencyName(useEnglishName = true))
    }

    @Test
    fun `toCurrencyName capitalizes device locale name`() {
        assertEquals("Российский Рубль", "RUB".toCurrencyName(useEnglishName = false))
    }
}

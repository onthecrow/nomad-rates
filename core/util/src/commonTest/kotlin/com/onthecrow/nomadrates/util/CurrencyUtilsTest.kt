package com.onthecrow.nomadrates.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyUtilsTest {

    @Test
    fun `toFlagAssetKey resolves special currencies to dedicated assets`() {
        assertEquals("btc", "BTC".toFlagAssetKey())
        assertEquals("xag", "XAG".toFlagAssetKey())
        assertEquals("xau", "XAU".toFlagAssetKey())
        assertEquals("xdr", "XDR".toFlagAssetKey())
        assertEquals("xpd", "XPD".toFlagAssetKey())
        assertEquals("xpt", "XPT".toFlagAssetKey())
        assertEquals("sh", "SHP".toFlagAssetKey())
    }

    @Test
    fun `toFlagAssetKey keeps existing country mappings intact`() {
        assertEquals("us", "USD".toFlagAssetKey())
        assertEquals("eu", "EUR".toFlagAssetKey())
        assertEquals("jp", "JPY".toFlagAssetKey())
    }

    @Test
    fun `toCurrencyName uses english name when requested`() {
        assertEquals("Russian Ruble", "RUB".toCurrencyName(useEnglishName = true))
    }

    @Test
    fun `capitalizeWords capitalizes the first letter of every word`() {
        assertEquals("Российский Рубль", "российский рубль".capitalizeWords())
        assertEquals("Доллар Тринидада И Тобаго", "доллар Тринидада и Тобаго".capitalizeWords())
        assertEquals("Peso Mexicano", "peso mexicano".capitalizeWords())
    }

    @Test
    fun `capitalizeWords keeps the rest of the word intact`() {
        assertEquals("Доллар США", "доллар США".capitalizeWords())
        assertEquals("US Dollar", "US Dollar".capitalizeWords())
        assertEquals("Bosnia-Herzegovina Convertible Mark", "Bosnia-Herzegovina Convertible Mark".capitalizeWords())
    }

    @Test
    fun `capitalizeWords skips leading punctuation`() {
        assertEquals("Franc CFA (Bceao)", "franc CFA (bceao)".capitalizeWords())
    }
}

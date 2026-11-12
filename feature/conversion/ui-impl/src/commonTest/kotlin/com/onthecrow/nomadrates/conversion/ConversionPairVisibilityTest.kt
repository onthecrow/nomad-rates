package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.mapper.toConversionListItems
import com.onthecrow.nomadrates.conversion.mapper.visiblePairs
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.currency.model.Currency
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionPairVisibilityTest {

    @Test
    fun `featured pair added to favourites stays listed when featured pairs are hidden`() {
        // EUR→USD comes from the remote config as featured; the user added it to favourites.
        val featuredFavourite = pair("EUR", "USD", isFeatured = true, isFavourite = true)
        val favourite = pair("USD", "EUR", isFeatured = false, isFavourite = true)

        val listed = listOf(featuredFavourite, favourite)
            .visiblePairs(showFeaturedPairs = false)
            .toConversionListItems()

        assertEquals(
            listOf(
                ("USD" to "EUR") to ListGroup.FAVOURITE,
                ("EUR" to "USD") to ListGroup.FAVOURITE,
            ),
            listed.dataItems(),
        )
    }

    @Test
    fun `featured pairs that are not favourites are hidden when featured pairs are hidden`() {
        val pairs = listOf(
            pair("EUR", "USD", isFeatured = true, isFavourite = false),
            pair("USD", "THB", isFeatured = false, isFavourite = true),
        )

        assertEquals(
            listOf(pair("USD", "THB", isFeatured = false, isFavourite = true)),
            pairs.visiblePairs(showFeaturedPairs = false),
        )
    }

    @Test
    fun `all pairs stay as they are when featured pairs are shown`() {
        val pairs = listOf(
            pair("EUR", "USD", isFeatured = true, isFavourite = true),
            pair("GBP", "USD", isFeatured = true, isFavourite = false),
            pair("USD", "THB", isFeatured = false, isFavourite = true),
        )

        assertEquals(pairs, pairs.visiblePairs(showFeaturedPairs = true))
        assertEquals(
            listOf(
                ("USD" to "THB") to ListGroup.FAVOURITE,
                ("EUR" to "USD") to ListGroup.FAVOURITE,
                ("GBP" to "USD") to ListGroup.FEATURED,
                ("EUR" to "USD") to ListGroup.FEATURED,
            ),
            pairs.toConversionListItems().dataItems(),
        )
    }

    private fun List<ConversionListItem>.dataItems(): List<Pair<Pair<String, String>, ListGroup>> {
        return filterIsInstance<ConversionListItem.Data>().map { item -> item.currencyPair to item.listGroup }
    }

    private fun pair(
        fromCode: String,
        toCode: String,
        isFeatured: Boolean,
        isFavourite: Boolean,
    ): ConversionPair {
        return ConversionPair(
            fromCurrency = currency(fromCode),
            toCurrency = currency(toCode),
            conversionRate = 1.0,
            historicalRates = listOf(1.0, 1.1),
            isFeatured = isFeatured,
            isFavourite = isFavourite,
        )
    }

    private fun currency(code: String): Currency {
        return Currency(
            code = code,
            conversionRate = 1.0,
            isFavourite = false,
            isFeatured = false,
            rates = listOf(1.0, 1.1),
        )
    }
}

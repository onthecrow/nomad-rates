package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_internet_required

class ConversionReducerTest {

    private val reducer = ConversionReducer()

    @Test
    fun `conversion data state maps to loading error and content`() = runTest {
        val pair = samplePair()

        val loadingState = reducer.reduce(
            state = ConversionState.Content(),
            event = ConversionEvent.OnConversionDataChanged(ConversionDataState.Loading),
        )
        val errorState = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(ConversionDataState.Error),
        )
        val contentState = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(
                ConversionDataState.Content(
                    activePair = pair,
                    conversionPairs = listOf(pair),
                )
            ),
        )

        assertEquals(ConversionState.Loading, loadingState)
        assertEquals(ConversionState.Error(Res.string.conversion_internet_required), errorState)
        assertIs<ConversionState.Content>(contentState)
        val conversionViewState = assertIs<ConversionViewState.Content>(contentState.conversionViewState)
        assertEquals("USD", conversionViewState.from.currencyCode)
        assertEquals(2, contentState.conversionListItems.size)
    }

    @Test
    fun `content mapping keeps entered values`() = runTest {
        val initialPair = samplePair()
        val updatedPair = samplePair(fromCode = "GBP", toCode = "JPY")

        val initialContent = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(
                ConversionDataState.Content(
                    activePair = initialPair,
                    conversionPairs = listOf(initialPair),
                )
            ),
        ) as ConversionState.Content

        val withInput = reducer.reduce(
            state = initialContent,
            event = ConversionEvent.OnFromValueChange("123.45"),
        )

        val updatedContent = reducer.reduce(
            state = withInput,
            event = ConversionEvent.OnConversionDataChanged(
                ConversionDataState.Content(
                    activePair = updatedPair,
                    conversionPairs = listOf(updatedPair),
                )
            ),
        ) as ConversionState.Content

        assertEquals("123.45", updatedContent.conversionViewState?.from?.conversionValue)
    }

    private fun samplePair(
        fromCode: String = "USD",
        toCode: String = "EUR",
    ): ConversionPair {
        return ConversionPair(
            fromCurrency = Currency(
                code = fromCode,
                conversionRate = 1.0,
                isFavourite = false,
                isFeatured = false,
                rates = listOf(1.0, 1.1),
            ),
            toCurrency = Currency(
                code = toCode,
                conversionRate = 0.9,
                isFavourite = false,
                isFeatured = false,
                rates = listOf(0.9, 1.0),
            ),
            conversionRate = 0.9,
            historicalRates = listOf(0.8, 0.9),
            isFeatured = true,
            isFavourite = false,
        )
    }
}

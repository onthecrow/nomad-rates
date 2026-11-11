package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConversionReducerTest {

    private val reducer = ConversionReducer()

    @Test
    fun `conversion data state maps to loading error and content`() = runTest {
        val pair = samplePair()

        val loadingState = reducer.reduce(
            state = ConversionState.Content(),
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Loading,
            ),
        )
        val errorState = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Error,
            ),
        )
        val contentState = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Content(
                    activePair = pair,
                    conversionPairs = listOf(pair),
                ),
            ),
        )

        assertEquals(ConversionState.Loading, loadingState)
        assertEquals(ConversionState.Error(), errorState)
        assertIs<ConversionState.Content>(contentState)
        val conversionViewState = assertIs<ConversionViewState.Content>(contentState.conversionViewState)
        assertEquals("USD", conversionViewState.from.currencyCode)
        assertEquals(2, contentState.conversionListItems.size)
    }

    @Test
    fun `retry state is preserved while loading and reset on error`() = runTest {
        val retryingErrorState = reducer.reduce(
            state = ConversionState.Error(),
            event = ConversionEvent.OnRetryStateChanged(isRetrying = true),
        )

        val loadingState = reducer.reduce(
            state = retryingErrorState,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Loading,
            ),
        )
        val errorState = reducer.reduce(
            state = retryingErrorState,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Error,
            ),
        )

        assertEquals(ConversionState.Error(isRetrying = true), retryingErrorState)
        assertEquals(ConversionState.Error(isRetrying = true), loadingState)
        assertEquals(ConversionState.Error(isRetrying = false), errorState)
    }

    @Test
    fun `content after retry exits error state`() = runTest {
        val pair = samplePair()

        val contentState = reducer.reduce(
            state = ConversionState.Error(isRetrying = true),
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Content(
                    activePair = pair,
                    conversionPairs = listOf(pair),
                ),
            ),
        )

        assertIs<ConversionState.Content>(contentState)
    }

    @Test
    fun `content mapping keeps entered values`() = runTest {
        val initialPair = samplePair()
        val updatedPair = samplePair(fromCode = "GBP", toCode = "JPY")

        val initialContent = reducer.reduce(
            state = ConversionState.Loading,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Content(
                    activePair = initialPair,
                    conversionPairs = listOf(initialPair),
                ),
            ),
        ) as ConversionState.Content

        val withInput = reducer.reduce(
            state = initialContent,
            event = ConversionEvent.OnFromValueChange("123.45"),
        )

        val updatedContent = reducer.reduce(
            state = withInput,
            event = ConversionEvent.OnConversionDataChanged(
                dataState = ConversionDataState.Content(
                    activePair = updatedPair,
                    conversionPairs = listOf(updatedPair),
                ),
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

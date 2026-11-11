package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.conversion.domain.model.BatchConversion
import com.onthecrow.nomadrates.conversion.domain.model.BatchConversionTarget
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.util.toCurrencyName
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class BatchConversionReducerTest {

    private val reducer = BatchConversionReducer()

    private val usd = currency("USD", rate = 1.0)
    private val eur = currency("EUR", rate = 0.5, isFavourite = true)
    private val gbp = currency("GBP", rate = 0.25, isFavourite = true)

    @Test
    fun `batch conversion data maps loading to content with default amount`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur, gbp)),
        )

        assertEquals("USD", content.baseCurrency.currencyCode)
        assertEquals(BatchConversionReducer.DEFAULT_AMOUNT, content.baseCurrency.conversionValue)
        assertEquals(listOf("EUR", "GBP"), content.favourites.map { it.currencyCode })
        assertEquals(
            listOf("EUR".toCurrencyName(useEnglishName = true), "GBP".toCurrencyName(useEnglishName = true)),
            content.favourites.map { it.currencyName },
        )
        assertEquals(
            listOf(formatted("0.5", "EUR"), formatted("0.25", "GBP")),
            content.favourites.map { it.convertedAmount },
        )
    }

    @Test
    fun `amount change converts into every favourite`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur, gbp)),
        )

        val updated = reducer.reduce(
            state = content,
            event = BatchConversionEvent.OnAmountChange("100"),
        )

        assertIs<BatchConversionState.Content>(updated)
        assertEquals("100", updated.baseCurrency.conversionValue)
        assertEquals(
            listOf(formatted("50", "EUR"), formatted("25", "GBP")),
            updated.favourites.map { it.convertedAmount },
        )
    }

    @Test
    fun `amount input is filtered like on the conversion screen`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur)),
        )

        val updated = reducer.reduce(
            state = content,
            event = BatchConversionEvent.OnAmountChange("1a,5.0"),
        )

        assertIs<BatchConversionState.Content>(updated)
        assertEquals("1.50", updated.baseCurrency.conversionValue)
        assertEquals(formatted("0.75", "EUR"), updated.favourites.single().convertedAmount)
    }

    @Test
    fun `empty or unfinished amount does not break conversion`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur)),
        )

        val empty = reducer.reduce(content, BatchConversionEvent.OnAmountChange(""))
        val separatorOnly = reducer.reduce(content, BatchConversionEvent.OnAmountChange("."))
        val trailingSeparator = reducer.reduce(content, BatchConversionEvent.OnAmountChange("12."))

        assertIs<BatchConversionState.Content>(empty)
        assertIs<BatchConversionState.Content>(separatorOnly)
        assertIs<BatchConversionState.Content>(trailingSeparator)
        assertEquals(formatted("0", "EUR"), empty.favourites.single().convertedAmount)
        assertEquals(formatted("0", "EUR"), separatorOnly.favourites.single().convertedAmount)
        assertEquals("12.", trailingSeparator.baseCurrency.conversionValue)
        assertEquals(formatted("6", "EUR"), trailingSeparator.favourites.single().convertedAmount)
    }

    @Test
    fun `entered amount is kept when base currency or favourites change`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur)),
        )
        val withAmount = reducer.reduce(content, BatchConversionEvent.OnAmountChange("200"))

        val updated = reduceData(
            state = withAmount,
            batchConversion = batchConversion(base = eur, targets = listOf(eur, gbp)),
        )

        assertEquals("EUR", updated.baseCurrency.currencyCode)
        assertEquals("200", updated.baseCurrency.conversionValue)
        assertEquals(listOf("EUR", "GBP"), updated.favourites.map { it.currencyCode })
        assertEquals(
            listOf(formatted("200", "EUR"), formatted("100", "GBP")),
            updated.favourites.map { it.convertedAmount },
        )
    }

    @Test
    fun `empty favourites map to empty list`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = emptyList()),
        )

        assertEquals(emptyList(), content.favourites)
    }

    @Test
    fun `amount change before data arrives keeps loading`() = runTest {
        val state = reducer.reduce(
            state = BatchConversionState.Loading,
            event = BatchConversionEvent.OnAmountChange("10"),
        )

        assertEquals(BatchConversionState.Loading, state)
    }

    @Test
    fun `navigation events do not change state`() = runTest {
        val content = reduceData(
            state = BatchConversionState.Loading,
            batchConversion = batchConversion(base = usd, targets = listOf(eur)),
        )

        listOf(
            BatchConversionEvent.OnBackPress,
            BatchConversionEvent.OnBaseCurrencyClick,
            BatchConversionEvent.OnAddCurrencyClick,
        ).forEach { event ->
            assertSame(content, reducer.reduce(content, event))
        }
    }

    private suspend fun reduceData(
        state: BatchConversionState,
        batchConversion: BatchConversion,
    ): BatchConversionState.Content {
        val reduced = reducer.reduce(
            state = state,
            event = BatchConversionEvent.OnBatchConversionChanged(
                batchConversion = batchConversion,
                useEnglishCurrencyNames = true,
            ),
        )
        return assertIs<BatchConversionState.Content>(reduced)
    }

    private fun batchConversion(
        base: Currency,
        targets: List<Currency>,
    ) = BatchConversion(
        baseCurrency = base,
        targets = targets.map { target ->
            BatchConversionTarget(
                currency = target,
                conversionRate = target.conversionRate / base.conversionRate,
            )
        },
    )

    private fun formatted(amount: String, currencyCode: String): String {
        return MoneyAmount(amount).formatAdaptive(currencyCode)
    }

    private fun currency(
        code: String,
        rate: Double,
        isFavourite: Boolean = false,
    ) = Currency(
        code = code,
        conversionRate = rate,
        isFavourite = isFavourite,
        isFeatured = false,
        rates = emptyList(),
    )
}

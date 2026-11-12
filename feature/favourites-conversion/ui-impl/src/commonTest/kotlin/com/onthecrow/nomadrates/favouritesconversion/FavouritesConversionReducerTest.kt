package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversionTarget
import com.onthecrow.nomadrates.favouritesconversion.model.CompactAmount
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.util.toCurrencyName
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class FavouritesConversionReducerTest {

    private val reducer = FavouritesConversionReducer()

    private val usd = currency("USD", rate = 1.0)
    private val eur = currency("EUR", rate = 0.5, isFavourite = true)
    private val gbp = currency("GBP", rate = 0.25, isFavourite = true)

    @Test
    fun `favourites conversion data maps loading to content with default amount`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur, gbp)),
        )

        assertEquals("USD", content.baseCurrency.currencyCode)
        assertEquals(FavouritesConversionReducer.DEFAULT_AMOUNT, content.baseCurrency.conversionValue)
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
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur, gbp)),
        )

        val updated = reducer.reduce(
            state = content,
            event = FavouritesConversionEvent.OnAmountChange("100"),
        )

        assertIs<FavouritesConversionState.Content>(updated)
        assertEquals("100", updated.baseCurrency.conversionValue)
        assertEquals(
            listOf(formatted("50", "EUR"), formatted("25", "GBP")),
            updated.favourites.map { it.convertedAmount },
        )
    }

    @Test
    fun `amount input is filtered like on the conversion screen`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        val updated = reducer.reduce(
            state = content,
            event = FavouritesConversionEvent.OnAmountChange("1a,5.0"),
        )

        assertIs<FavouritesConversionState.Content>(updated)
        assertEquals("1.50", updated.baseCurrency.conversionValue)
        assertEquals(formatted("0.75", "EUR"), updated.favourites.single().convertedAmount)
    }

    @Test
    fun `empty or unfinished amount does not break conversion`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        val empty = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange(""))
        val separatorOnly = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("."))
        val trailingSeparator = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("12."))

        assertIs<FavouritesConversionState.Content>(empty)
        assertIs<FavouritesConversionState.Content>(separatorOnly)
        assertIs<FavouritesConversionState.Content>(trailingSeparator)
        assertEquals("0", empty.baseCurrency.conversionValue)
        assertEquals(formatted("0", "EUR"), empty.favourites.single().convertedAmount)
        assertEquals("0.", separatorOnly.baseCurrency.conversionValue)
        assertEquals(formatted("0", "EUR"), separatorOnly.favourites.single().convertedAmount)
        assertEquals("12.", trailingSeparator.baseCurrency.conversionValue)
        assertEquals(formatted("6", "EUR"), trailingSeparator.favourites.single().convertedAmount)
    }

    @Test
    fun `amount is kept without leading zeros`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        val typed = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("05"))
        val zeros = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("00"))

        assertEquals("5", typed.amount)
        assertEquals("0", zeros.amount)
    }

    @Test
    fun `huge amounts also get a compact form`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        val small = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("1000"))
        val huge = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("4320000000000000000000"))

        assertIs<FavouritesConversionState.Content>(small)
        assertIs<FavouritesConversionState.Content>(huge)
        assertEquals(null, small.favourites.single().compactAmount)
        assertEquals(
            CompactAmount(mantissa = formatted("2.16", "EUR"), exponent = 21),
            huge.favourites.single().compactAmount,
        )
    }

    @Test
    fun `entered amount is kept when base currency or favourites change`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )
        val withAmount = reducer.reduce(content, FavouritesConversionEvent.OnAmountChange("200"))

        val updated = reduceData(
            state = withAmount,
            favouritesConversion = favouritesConversion(base = eur, targets = listOf(eur, gbp)),
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
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = emptyList()),
        )

        assertEquals(emptyList(), content.favourites)
    }

    @Test
    fun `amount change before data arrives is used once data arrives`() = runTest {
        val loading = reducer.reduce(
            state = FavouritesConversionState.Loading(),
            event = FavouritesConversionEvent.OnAmountChange("10"),
        )

        assertEquals(FavouritesConversionState.Loading(amount = "10"), loading)

        val content = reduceData(
            state = loading,
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        assertEquals("10", content.baseCurrency.conversionValue)
        assertEquals(formatted("5", "EUR"), content.favourites.single().convertedAmount)
    }

    @Test
    fun `opened screen shows the given currency and amount before data arrives`() = runTest {
        val loading = reducer.reduce(
            state = FavouritesConversionState.Loading(),
            event = FavouritesConversionEvent.OnOpened(baseCurrencyCode = "USD", amount = "250.5"),
        )

        assertIs<FavouritesConversionState.Loading>(loading)
        val amountField = assertIs<CurrencyAmountViewState.Content>(loading.amountField)
        assertEquals("USD", amountField.currencyCode)
        assertEquals("250.5", amountField.conversionValue)

        val content = reduceData(
            state = loading,
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        assertEquals("250.5", content.baseCurrency.conversionValue)
        assertEquals(formatted("125.25", "EUR"), content.favourites.single().convertedAmount)
    }

    @Test
    fun `opened screen without currency or amount keeps loading field and default amount`() = runTest {
        val loading = reducer.reduce(
            state = FavouritesConversionState.Loading(),
            event = FavouritesConversionEvent.OnOpened(baseCurrencyCode = "", amount = " "),
        )

        assertEquals(FavouritesConversionState.Loading(), loading)
        assertEquals(CurrencyAmountViewState.Loading, loading.amountField)
        assertEquals(FavouritesConversionReducer.DEFAULT_AMOUNT, loading.amount)
    }

    @Test
    fun `removed currency disappears from the list right away`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur, gbp)),
        )

        val updated = reducer.reduce(content, FavouritesConversionEvent.OnRemoveCurrency("EUR"))

        assertIs<FavouritesConversionState.Content>(updated)
        assertEquals(listOf("GBP"), updated.favourites.map { it.currencyCode })
        assertEquals(content.baseCurrency, updated.baseCurrency)
    }

    @Test
    fun `removing a currency while loading keeps loading`() = runTest {
        val loading = FavouritesConversionState.Loading(amount = "5")

        assertSame(loading, reducer.reduce(loading, FavouritesConversionEvent.OnRemoveCurrency("EUR")))
    }

    @Test
    fun `navigation events do not change state`() = runTest {
        val content = reduceData(
            state = FavouritesConversionState.Loading(),
            favouritesConversion = favouritesConversion(base = usd, targets = listOf(eur)),
        )

        listOf(
            FavouritesConversionEvent.OnBackPress,
            FavouritesConversionEvent.OnBaseCurrencyClick,
            FavouritesConversionEvent.OnAddCurrencyClick,
        ).forEach { event ->
            assertSame(content, reducer.reduce(content, event))
        }
    }

    private suspend fun reduceData(
        state: FavouritesConversionState,
        favouritesConversion: FavouritesConversion,
    ): FavouritesConversionState.Content {
        val reduced = reducer.reduce(
            state = state,
            event = FavouritesConversionEvent.OnFavouritesConversionChanged(
                favouritesConversion = favouritesConversion,
                useEnglishCurrencyNames = true,
            ),
        )
        return assertIs<FavouritesConversionState.Content>(reduced)
    }

    private fun favouritesConversion(
        base: Currency,
        targets: List<Currency>,
    ) = FavouritesConversion(
        baseCurrency = base,
        targets = targets.map { target ->
            FavouritesConversionTarget(
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

package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveFavouritesConversionUseCaseImplTest {

    private val usd = currency("USD", rate = 1.0)
    private val eur = currency("EUR", rate = 0.5, isFavourite = true)
    private val gbp = currency("GBP", rate = 0.25, isFavourite = true)
    private val jpy = currency("JPY", rate = 150.0)

    @Test
    fun `uses from currency as base and converts into favourites sorted by code`() = runTest {
        val useCase = ObserveFavouritesConversionUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                MutableStateFlow(SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR")),
            ),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(
                MutableStateFlow(listOf(usd, jpy, gbp, eur)),
            ),
        )

        val favouritesConversion = useCase(ConversionPairSide.FROM).first()

        assertEquals(usd, favouritesConversion.baseCurrency)
        assertEquals(listOf("EUR", "GBP"), favouritesConversion.targets.map { it.currency.code })
        assertEquals(listOf(0.5, 0.25), favouritesConversion.targets.map { it.conversionRate })
    }

    @Test
    fun `follows changes of the from currency`() = runTest {
        val selectedPair = MutableStateFlow(SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR"))
        val useCase = ObserveFavouritesConversionUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(selectedPair),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(
                MutableStateFlow(listOf(usd, eur, gbp)),
            ),
        )

        selectedPair.value = SelectedConversionPair(fromCurrencyCode = "EUR", toCurrencyCode = "GBP")
        val favouritesConversion = useCase(ConversionPairSide.FROM).first()

        assertEquals(eur, favouritesConversion.baseCurrency)
        assertEquals(listOf(1.0, 0.5), favouritesConversion.targets.map { it.conversionRate })
    }

    @Test
    fun `new favourite appears in targets`() = runTest {
        val currencies = MutableStateFlow(listOf(usd, eur, jpy))
        val useCase = ObserveFavouritesConversionUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                MutableStateFlow(SelectedConversionPair.DEFAULT),
            ),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(currencies),
        )

        assertEquals(listOf("EUR"), useCase(ConversionPairSide.FROM).first().targets.map { it.currency.code })

        currencies.value = listOf(usd, eur, jpy.copy(isFavourite = true))

        assertEquals(listOf("EUR", "JPY"), useCase(ConversionPairSide.FROM).first().targets.map { it.currency.code })
    }

    @Test
    fun `to side uses the to currency as base`() = runTest {
        val useCase = ObserveFavouritesConversionUseCaseImpl(
            getSelectedConversionPairUseCase = FakeGetSelectedConversionPairUseCase(
                MutableStateFlow(SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR")),
            ),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(
                MutableStateFlow(listOf(usd, eur, gbp)),
            ),
        )

        val favouritesConversion = useCase(ConversionPairSide.TO).first()

        assertEquals(eur, favouritesConversion.baseCurrency)
        assertEquals(listOf(1.0, 0.5), favouritesConversion.targets.map { it.conversionRate })
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

    private class FakeGetSelectedConversionPairUseCase(
        private val flow: Flow<SelectedConversionPair>,
    ) : GetSelectedConversionPairUseCase {
        override fun invoke(): Flow<SelectedConversionPair> = flow
    }

    private class FakeGetCurrencyListUseCase(
        private val flow: Flow<List<Currency>?>,
    ) : GetCurrencyListUseCase {
        override fun invoke(): Flow<List<Currency>?> = flow
    }
}

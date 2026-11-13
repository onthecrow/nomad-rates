package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConvertCurrenciesUseCaseImplTest {

    @Test
    fun `converts with saved rates while the remote response is missing`() = runTest {
        val useCase = ConvertCurrenciesUseCaseImpl(
            FakeCurrencyRepository(currency("USD", 1.0), currency("EUR", 0.8)),
        )

        assertEquals(2_320.0, useCase(fromCurrencyCode = "EUR", toCurrencyCode = "USD", amount = 1_856.0)!!, 1e-9)
        assertEquals(1_484.8, useCase(fromCurrencyCode = "USD", toCurrencyCode = "EUR", amount = 1_856.0)!!, 1e-9)
    }

    @Test
    fun `returns null instead of the same amount when a rate is missing`() = runTest {
        val useCase = ConvertCurrenciesUseCaseImpl(FakeCurrencyRepository(currency("EUR", 0.8)))

        assertNull(useCase(fromCurrencyCode = "EUR", toCurrencyCode = "USD", amount = 1_856.0))
        assertNull(useCase(fromCurrencyCode = "USD", toCurrencyCode = "EUR", amount = 1_856.0))
    }

    private fun currency(code: String, rate: Double) = Currency(
        code = code,
        conversionRate = rate,
        isFavourite = false,
        isFeatured = false,
        rates = emptyList(),
    )

    /** Saved rates only: the remote response, and so the base currency, is not there. */
    private class FakeCurrencyRepository(vararg currencies: Currency) : CurrencyRepository {
        private val currencies = currencies.associateBy { it.code }

        override fun getCurrencyList(): Flow<List<Currency>?> = flowOf(currencies.values.toList())
        override suspend fun getCurrency(currencyCode: String): Currency? = currencies[currencyCode]
        override fun getCurrencyFlow(currencyCode: String): Flow<Currency?> = flowOf(currencies[currencyCode])
        override fun getBaseCurrency(): Flow<Currency?> = flowOf(null)
        override fun refreshCurrencies() = Unit
        override suspend fun saveCurrency(currency: Currency) = Unit
    }
}

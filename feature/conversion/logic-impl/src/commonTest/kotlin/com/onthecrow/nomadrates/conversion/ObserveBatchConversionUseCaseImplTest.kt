package com.onthecrow.nomadrates.conversion

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Path.Companion.toPath

class ObserveBatchConversionUseCaseImplTest {

    private val usd = currency("USD", rate = 1.0)
    private val eur = currency("EUR", rate = 0.5, isFavourite = true)
    private val gbp = currency("GBP", rate = 0.25, isFavourite = true)
    private val jpy = currency("JPY", rate = 150.0)

    @Test
    fun `defaults to usd base and converts into favourites sorted by code`() = runTest {
        val useCase = ObserveBatchConversionUseCaseImpl(
            conversionPreferencesDataSource = createPreferencesDataSource(backgroundScope),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(
                MutableStateFlow(listOf(usd, jpy, gbp, eur)),
            ),
        )

        val batchConversion = useCase().first()

        assertEquals(usd, batchConversion.baseCurrency)
        assertEquals(listOf("EUR", "GBP"), batchConversion.targets.map { it.currency.code })
        assertEquals(listOf(0.5, 0.25), batchConversion.targets.map { it.conversionRate })
    }

    @Test
    fun `saved base currency is used for rates`() = runTest {
        val dataSource = createPreferencesDataSource(backgroundScope)
        dataSource.saveBatchBaseCurrencyCode("EUR")
        val useCase = ObserveBatchConversionUseCaseImpl(
            conversionPreferencesDataSource = dataSource,
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(
                MutableStateFlow(listOf(usd, eur, gbp)),
            ),
        )

        val batchConversion = useCase().first()

        assertEquals(eur, batchConversion.baseCurrency)
        assertEquals(listOf(1.0, 0.5), batchConversion.targets.map { it.conversionRate })
    }

    @Test
    fun `new favourite appears in targets`() = runTest {
        val currencies = MutableStateFlow(listOf(usd, eur, jpy))
        val useCase = ObserveBatchConversionUseCaseImpl(
            conversionPreferencesDataSource = createPreferencesDataSource(backgroundScope),
            getCurrencyListUseCase = FakeGetCurrencyListUseCase(currencies),
        )

        assertEquals(listOf("EUR"), useCase().first().targets.map { it.currency.code })

        currencies.value = listOf(usd, eur, jpy.copy(isFavourite = true))

        assertEquals(listOf("EUR", "JPY"), useCase().first().targets.map { it.currency.code })
    }

    private fun createPreferencesDataSource(
        scope: CoroutineScope,
    ): ConversionPreferencesDataSource {
        return ConversionPreferencesDataSource(
            PreferenceDataStoreFactory.createWithPath(
                scope = scope,
                produceFile = {
                    "/tmp/batch_conversion_${Random.nextInt()}.preferences_pb".toPath()
                },
            ),
        )
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

    private class FakeGetCurrencyListUseCase(
        private val flow: Flow<List<Currency>?>,
    ) : GetCurrencyListUseCase {
        override fun invoke(): Flow<List<Currency>?> = flow
    }
}

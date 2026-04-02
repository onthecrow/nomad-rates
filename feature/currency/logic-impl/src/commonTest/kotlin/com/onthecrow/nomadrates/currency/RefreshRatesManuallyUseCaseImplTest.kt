package com.onthecrow.nomadrates.currency

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.Path.Companion.toPath

class RefreshRatesManuallyUseCaseImplTest {

    @Test
    fun `skips real refresh when last manual refresh is inside threshold`() = runTest {
        val metadataDataSource = createMetadataDataSource()
        val refreshUseCase = FakeRefreshCurrenciesUseCase()
        val remoteDataSource = TestCurrencyRemoteDataSource()

        metadataDataSource.saveLastManualRefreshAt(1_000L)

        val useCase = RefreshRatesManuallyUseCaseImpl(
            currencyRatesMetadataDataSource = metadataDataSource,
            refreshCurrenciesUseCase = refreshUseCase,
            currencyRemoteDataSource = remoteDataSource,
            currentTimeMillisProvider = { 2_000L },
            fakeLoadingDurationMillis = 0L,
            refreshThresholdMillis = 10_000L,
        )

        useCase()

        assertEquals(0, refreshUseCase.invocationCount)
        assertEquals(2_000L, metadataDataSource.getLastManualRefreshAt())
    }

    @Test
    fun `invokes real refresh when threshold has expired`() = runTest {
        val metadataDataSource = createMetadataDataSource()
        val refreshUseCase = FakeRefreshCurrenciesUseCase()
        val remoteDataSource = TestCurrencyRemoteDataSource()

        metadataDataSource.saveLastManualRefreshAt(1_000L)

        val useCase = RefreshRatesManuallyUseCaseImpl(
            currencyRatesMetadataDataSource = metadataDataSource,
            refreshCurrenciesUseCase = refreshUseCase,
            currencyRemoteDataSource = remoteDataSource,
            currentTimeMillisProvider = { 20_000L },
            fakeLoadingDurationMillis = 0L,
            refreshThresholdMillis = 10_000L,
        )

        backgroundScope.launch {
            remoteDataSource.emitSuccess()
        }

        useCase()

        assertEquals(1, refreshUseCase.invocationCount)
        assertEquals(20_000L, metadataDataSource.getLastManualRefreshAt())
    }

    @Test
    fun `does not update manual refresh timestamp when real refresh fails`() = runTest {
        val metadataDataSource = createMetadataDataSource()
        val refreshUseCase = FakeRefreshCurrenciesUseCase()
        val remoteDataSource = TestCurrencyRemoteDataSource()

        val useCase = RefreshRatesManuallyUseCaseImpl(
            currencyRatesMetadataDataSource = metadataDataSource,
            refreshCurrenciesUseCase = refreshUseCase,
            currencyRemoteDataSource = remoteDataSource,
            currentTimeMillisProvider = { 20_000L },
            fakeLoadingDurationMillis = 0L,
            refreshThresholdMillis = 10_000L,
            refreshCompletionTimeoutMillis = 10L,
        )

        useCase()

        assertEquals(1, refreshUseCase.invocationCount)
        assertEquals(null, metadataDataSource.getLastManualRefreshAt())
    }

    private fun createMetadataDataSource(): CurrencyRatesMetadataDataSource {
        return CurrencyRatesMetadataDataSource(
            PreferenceDataStoreFactory.createWithPath(
                scope = backgroundScope,
                produceFile = { "/tmp/refresh_rates_${Random.nextInt()}.preferences_pb".toPath() },
            )
        )
    }

    private class FakeRefreshCurrenciesUseCase : RefreshCurrenciesUseCase {
        var invocationCount: Int = 0

        override fun invoke() {
            invocationCount++
        }
    }

    private class TestCurrencyRemoteDataSource : CurrencyRemoteDataSource() {
        fun emitSuccess() {
            emitState(Result.success(createPayload()))
        }

        override fun getString(key: String): String = ""

        override fun getKeysByPrefix(prefix: String): Set<String> = emptySet()

        override fun startBackgroundSync(onActivated: () -> Unit, onError: (Throwable) -> Unit) = Unit

        override suspend fun fetchAndActivate(): Boolean = true
    }

    private fun createPayload(): com.onthecrow.nomadrates.currency.data.CurrencyConfigPayload {
        return com.onthecrow.nomadrates.currency.data.CurrencyConfigPayload(
            config = CurrenciesResponse(
                disclaimer = "test",
                license = "test",
                timestamp = 1L,
                base = "USD",
                rates = mapOf("USD" to 1.0),
            ),
            historical = mapOf("USD" to listOf(1.0)),
        )
    }
}

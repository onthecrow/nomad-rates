package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshRatesManuallyUseCase
import com.onthecrow.nomadrates.remoteconfig.MIN_REMOTE_CONFIG_FETCH_WINDOW
import com.onthecrow.nomadrates.util.ApplicationUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

internal class RefreshRatesManuallyUseCaseImpl(
    private val currencyRatesMetadataDataSource: CurrencyRatesMetadataDataSource,
    private val refreshCurrenciesUseCase: RefreshCurrenciesUseCase,
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currentTimeMillisProvider: () -> Long = { ApplicationUtils.currentTimeMillis() },
    private val fakeLoadingDurationMillis: Long = DEFAULT_FAKE_LOADING_DURATION_MS,
    private val refreshThresholdMillis: Long = MIN_REMOTE_CONFIG_FETCH_WINDOW.inWholeMilliseconds * 2,
    private val refreshCompletionTimeoutMillis: Long = REFRESH_COMPLETION_TIMEOUT_MS,
) : RefreshRatesManuallyUseCase {
    override suspend fun invoke() {
        val now = currentTimeMillisProvider()
        val lastManualRefreshAt = currencyRatesMetadataDataSource.getLastManualRefreshAt()

        if (lastManualRefreshAt != null && now - lastManualRefreshAt < refreshThresholdMillis) {
            delay(fakeLoadingDurationMillis)
            currencyRatesMetadataDataSource.saveLastManualRefreshAt(currentTimeMillisProvider())
            return
        }

        refreshCurrenciesUseCase()

        val refreshResult = withTimeoutOrNull(refreshCompletionTimeoutMillis) {
            currencyRemoteDataSource.state
                .filterNotNull()
                .first()
        }

        if (refreshResult?.isSuccess == true) {
            currencyRatesMetadataDataSource.saveLastManualRefreshAt(currentTimeMillisProvider())
        }
    }

    private companion object {
        private const val DEFAULT_FAKE_LOADING_DURATION_MS = 1_000L
        private const val REFRESH_COMPLETION_TIMEOUT_MS = 12_000L
    }
}

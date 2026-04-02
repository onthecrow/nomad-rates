package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.domain.ObserveLastRatesTimestampUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveLastRatesTimestampUseCaseImpl(
    private val currencyRatesMetadataDataSource: CurrencyRatesMetadataDataSource,
) : ObserveLastRatesTimestampUseCase {
    override fun invoke(): Flow<Long?> {
        return currencyRatesMetadataDataSource.observeLatestVisibleRatesTimestamp()
    }
}

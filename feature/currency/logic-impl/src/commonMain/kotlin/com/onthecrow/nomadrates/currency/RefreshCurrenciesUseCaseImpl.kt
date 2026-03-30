package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider

internal class RefreshCurrenciesUseCaseImpl(
    private val currencyRepository: CurrencyRepository,
    private val remoteConfigProvider: RemoteConfigProvider,
) : RefreshCurrenciesUseCase {
    override fun invoke() {
        remoteConfigProvider.refresh()
        currencyRepository.refreshCurrencies()
    }
}

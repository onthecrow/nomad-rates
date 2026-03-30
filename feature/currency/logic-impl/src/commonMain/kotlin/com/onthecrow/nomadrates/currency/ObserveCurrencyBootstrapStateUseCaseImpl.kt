package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRemoteDataSource
import com.onthecrow.nomadrates.currency.domain.CurrencyBootstrapState
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.domain.ObserveCurrencyBootstrapStateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ObserveCurrencyBootstrapStateUseCaseImpl(
    private val getCurrencyListUseCase: GetCurrencyListUseCase,
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
) : ObserveCurrencyBootstrapStateUseCase {

    override fun invoke(): Flow<CurrencyBootstrapState> {
        return combine(
            getCurrencyListUseCase(),
            currencyRemoteDataSource.state,
        ) { currencies, remoteState ->
            when {
                !currencies.isNullOrEmpty() -> CurrencyBootstrapState.Cached
                remoteState?.isFailure == true -> CurrencyBootstrapState.Error
                else -> CurrencyBootstrapState.Loading
            }
        }.distinctUntilChanged()
    }
}

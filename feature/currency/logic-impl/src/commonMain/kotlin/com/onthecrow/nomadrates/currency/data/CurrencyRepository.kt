package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyRepository(
    private val currencyRemoteConfigDataSource: CurrencyRemoteConfigDataSource,
) {
    fun getCurrencyList(): Flow<List<Currency>?> {
        return currencyRemoteConfigDataSource.configDataFlow
            .map { currenciesResponse: CurrenciesResponse? ->
                currenciesResponse?.rates?.map { (code, _) -> Currency(code) }
            }
    }
}

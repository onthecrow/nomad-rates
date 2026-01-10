package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.CurrenciesResponse
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class CurrencyRepositoryImpl(
    private val currencyRemoteConfigDataSource: CurrencyRemoteConfigDataSource,
): CurrencyRepository {
    override fun getCurrencyList(): Flow<List<Currency>?> {
        return currencyRemoteConfigDataSource.configDataFlow
            .map { currenciesResponse: CurrenciesResponse? ->
                currenciesResponse?.rates?.map { (code, rate) ->
                    Currency(
                        code,
                        conversionRate = rate
                    )
                }
            }
    }

    override fun getCurrency(currencyCode: String): Flow<Currency?> {
        return currencyRemoteConfigDataSource.configDataFlow
            .map { response ->
                val rate = response?.rates?.get(currencyCode)
                rate?.let { Currency(code = currencyCode, conversionRate = it) }
            }
            .distinctUntilChanged()
    }

    override fun getBaseCurrency(): Flow<Currency?> {
        return currencyRemoteConfigDataSource.configDataFlow
            // TODO implement a proper error handling
            .map { currenciesResponse ->
                val base = currenciesResponse?.base ?: return@map null
                val rate = currenciesResponse.rates[base] ?: return@map null
                return@map Currency(code = base, conversionRate = rate)
            }
            .distinctUntilChanged()
    }
}

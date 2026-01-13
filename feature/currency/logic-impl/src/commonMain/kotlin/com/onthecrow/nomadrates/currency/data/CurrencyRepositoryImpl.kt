package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class CurrencyRepositoryImpl(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currencyDatabaseDataSource: CurrencyDatabaseDataSource,
) : CurrencyRepository {
    override fun getCurrencyList(): Flow<List<Currency>?> {
        return channelFlow {
            launch {
                currencyRemoteDataSource.configDataFlow.collect { currenciesResponse ->
                    val currencies = currenciesResponse?.rates?.map { (code, rate) ->
                        Currency(
                            code = code,
                            conversionRate = rate
                        )
                    }
                    println("Got currencies from config: $currencies")

                    if (currencies != null) {
                        currencyDatabaseDataSource.saveCurrencies(currencies)
                    }
                }
            }

            currencyDatabaseDataSource.getCurrencies()
                .collect { currencies -> send(currencies) }
        }
    }

    override fun getCurrency(currencyCode: String): Flow<Currency?> {
        return channelFlow {
            launch {
                currencyRemoteDataSource.configDataFlow
                    .map { response ->
                        val rate = response?.rates?.get(currencyCode)
                        rate?.let { Currency(code = currencyCode, conversionRate = it) }
                    }
                    .distinctUntilChanged()
                    .collect { currency ->
                        // todo error handling if currency is null?
                        currencyDatabaseDataSource.saveCurrency(currency ?: return@collect)
                    }
            }

            currencyDatabaseDataSource.getCurrency(currencyCode)
                .collect { currency -> send(currency) }
        }
    }

    override fun getBaseCurrency(): Flow<Currency?> {
        return currencyRemoteDataSource.configDataFlow
            // TODO implement a proper error handling
            // TODO return it from db as well
            .map { currenciesResponse ->
                val base = currenciesResponse?.base ?: return@map null
                val rate = currenciesResponse.rates[base] ?: return@map null
                return@map Currency(code = base, conversionRate = rate)
            }
            .distinctUntilChanged()
    }
}

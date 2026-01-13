package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CurrencyRepositoryImpl(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currencyDatabaseDataSource: CurrencyDatabaseDataSource,
    private val remoteConfigProvider: RemoteConfigProvider,
) : CurrencyRepository {
    override fun getCurrencyList(): Flow<List<Currency>?> {
        return channelFlow {
            combine(
                remoteConfigProvider.getRemoteConfigFlow(),
                currencyRemoteDataSource.configDataFlow,
            ) { remoteConfig, currenciesResponse ->
                val currencies = currenciesResponse?.rates?.map { (code, rate) ->
                    Currency(
                        code = code,
                        conversionRate = rate,
                        isFeatured = code in remoteConfig.featuredCurrencies,
                        isFavourite = false,
                    )
                }

                if (currencies != null) {
                    currencyDatabaseDataSource.saveCurrencies(currencies)
                }
            }.launchIn(this)

            currencyDatabaseDataSource.getCurrencies()
                .collect { currencies -> send(currencies) }
        }
    }

    override fun getCurrency(currencyCode: String): Flow<Currency?> {
        return channelFlow {
            combine(
                currencyRemoteDataSource.configDataFlow,
                remoteConfigProvider.getRemoteConfigFlow(),
            ) { currenciesResponse, remoteConfig ->
                val rate = currenciesResponse?.rates?.get(currencyCode)
                rate?.let {
                    Currency(
                        code = currencyCode,
                        conversionRate = it,
                        isFeatured = currencyCode in remoteConfig.featuredCurrencies,
                        isFavourite = false,
                    )
                }
            }
                .distinctUntilChanged()
                .onEach { currency ->
                    currencyDatabaseDataSource.saveCurrency(currency ?: return@onEach)
                }
                .launchIn(this)

            currencyDatabaseDataSource.getCurrencyFlow(currencyCode)
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
                return@map Currency(code = base, conversionRate = rate, isFeatured = false, isFavourite = false)
            }
            .distinctUntilChanged()
    }
}

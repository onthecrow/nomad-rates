package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class CurrencyRepositoryImpl(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currencyDatabaseDataSource: CurrencyDatabaseDataSource,
    private val remoteConfigProvider: RemoteConfigProvider,
) : CurrencyRepository {
    override fun getCurrencyList(): Flow<List<Currency>?> {
        // TODO make this offline-first logic reusable
        return channelFlow {
            combine(
                remoteConfigProvider.getRemoteConfigFlow(),
                currencyRemoteDataSource.configDataFlow,
                currencyRemoteDataSource.historicalDataFlow,
            ) { remoteConfig, currenciesResponse, historical ->
                // todo possible race condition, maybe need to do it atomically or/and in domain
                val localCurrenciesMap = currencyDatabaseDataSource.getCurrencies().first()
                    .associateBy { currency -> currency.code }
                val currencies = currenciesResponse?.rates?.map { (code, rate) ->
                    val localCurrency = localCurrenciesMap[code]
                    Currency(
                        code = code,
                        conversionRate = rate,
                        isFeatured = code in remoteConfig.featuredCurrencies,
                        isFavourite = localCurrency?.isFavourite ?: false,
                        rates = historical?.get(code) ?: emptyList(),
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

    override fun getCurrencyFlow(currencyCode: String): Flow<Currency?> {
        return channelFlow {
            combine(
                currencyRemoteDataSource.configDataFlow,
                remoteConfigProvider.getRemoteConfigFlow(),
                currencyRemoteDataSource.historicalDataFlow,
            ) { currenciesResponse, remoteConfig, historical ->
                val rate = currenciesResponse?.rates?.get(currencyCode)
                rate?.let {
                    Currency(
                        code = currencyCode,
                        conversionRate = it,
                        isFeatured = currencyCode in remoteConfig.featuredCurrencies,
                        isFavourite = currencyDatabaseDataSource.getCurrency(currencyCode)?.isFavourite
                            ?: false,
                        rates = historical?.get(currencyCode) ?: emptyList(),
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
        return combine(
            currencyRemoteDataSource.configDataFlow,
            currencyRemoteDataSource.historicalDataFlow,
        ) { currenciesResponse, historical ->
            // TODO implement a proper error handling
            // TODO return it from db as well
            val base = currenciesResponse?.base ?: return@combine null
            val rate = currenciesResponse.rates[base] ?: return@combine null
            Currency(
                code = base,
                conversionRate = rate,
                isFeatured = false,
                isFavourite = false,
                rates = historical?.get(base) ?: emptyList(),
            )
        }.distinctUntilChanged()
    }

    override suspend fun saveCurrency(currency: Currency) {
        currencyDatabaseDataSource.saveCurrency(currency)
    }
}

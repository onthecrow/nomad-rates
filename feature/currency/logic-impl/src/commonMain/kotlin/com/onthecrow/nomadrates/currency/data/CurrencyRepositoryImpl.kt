package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.remoteconfig.RemoteConfig
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal class CurrencyRepositoryImpl(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currencyDatabaseDataSource: CurrencyDatabaseDataSource,
    private val currencyRatesMetadataDataSource: CurrencyRatesMetadataDataSource,
    remoteConfigProvider: RemoteConfigProvider,
) : CurrencyRepository {

    init {
        combine(
            currencyRemoteDataSource.state,
            remoteConfigProvider.getRemoteConfigFlow()
                .onStart { emit(RemoteConfig(emptyList(), emptyList())) },
        ) { currenciesResponse, remoteConfig ->

            val localCurrenciesMap = currencyDatabaseDataSource.getCurrencies().first()
                .associateBy { currency -> currency.code }
            val timestamp = currenciesResponse?.getOrNull()?.config?.timestamp
            val historical = currenciesResponse?.getOrNull()?.historical
            val currencies = currenciesResponse?.getOrNull()?.config?.rates?.map { (code, rate) ->
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
                if (timestamp != null) {
                    currencyRatesMetadataDataSource.saveLastRatesTimestampIfNewer(timestamp)
                }
            }
        }
            .launchIn(MainScope())
    }


    override fun getCurrencyList(): Flow<List<Currency>?> {
        return currencyDatabaseDataSource.getCurrencies()
    }

    override suspend fun getCurrency(currencyCode: String): Currency? {
        return currencyDatabaseDataSource.getCurrency(currencyCode)
    }


    override fun getCurrencyFlow(currencyCode: String): Flow<Currency?> {
        return currencyDatabaseDataSource.getCurrencyFlow(currencyCode)
    }

    override fun getBaseCurrency(): Flow<Currency?> {
        return currencyRemoteDataSource.state.map { currenciesResponse ->
            // TODO implement a proper error handling
            // TODO return it from db as well
            val config = currenciesResponse?.getOrNull()?.config
            val base = config?.base ?: return@map null
            val rate = config.rates[base] ?: return@map null
            Currency(
                code = base,
                conversionRate = rate,
                isFeatured = false,
                isFavourite = false,
                rates = currenciesResponse.getOrNull()?.historical?.get(base) ?: emptyList(),
            )
        }.distinctUntilChanged()
    }

    override fun refreshCurrencies() {
        currencyRemoteDataSource.refresh()
    }

    override suspend fun saveCurrency(currency: Currency) {
        currencyDatabaseDataSource.saveCurrency(currency)
    }
}

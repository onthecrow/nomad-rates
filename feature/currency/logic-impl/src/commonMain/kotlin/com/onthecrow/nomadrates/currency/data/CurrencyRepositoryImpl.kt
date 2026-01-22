package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn

internal class CurrencyRepositoryImpl(
    private val currencyRemoteDataSource: CurrencyRemoteDataSource,
    private val currencyDatabaseDataSource: CurrencyDatabaseDataSource,
    remoteConfigProvider: RemoteConfigProvider,
) : CurrencyRepository {

    init {
        combine(
            currencyRemoteDataSource.configDataFlow,
            remoteConfigProvider.getRemoteConfigFlow(),
            currencyRemoteDataSource.historicalDataFlow,
        ) { currenciesResponse, remoteConfig, historical ->

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

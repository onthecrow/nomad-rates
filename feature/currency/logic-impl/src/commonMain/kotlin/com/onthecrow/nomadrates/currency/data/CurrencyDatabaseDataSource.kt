package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// TODO implement propper mapping here data -> domain, or may be move it to repository (?)
class CurrencyDatabaseDataSource(
    private val currencyDao: CurrencyDao,
) {
    fun getCurrencies(): Flow<List<Currency>> {
        return currencyDao.getAllCurrenciesFlow()
            .map { currencies ->
                println("Got currencies from database: $currencies")
                currencies.map { currency ->
                    Currency(
                        currency.currencyCode,
                        currency.conversionRate
                    )
                }
            }
    }

    suspend fun saveCurrencies(currencies: List<Currency>) {
        val currencyEntities = currencies.map { currency ->
            CurrencyEntity(
                id = currency.code,
                currencyCode = currency.code,
                conversionRate = currency.conversionRate,
                isFavourite = false,
                isFeatured = false,
            )
        }
        currencyDao.insertAll(currencyEntities)
    }

    suspend fun saveCurrency(currency: Currency) {
        val currencyEntity = CurrencyEntity(
            id = currency.code,
            currencyCode = currency.code,
            conversionRate = currency.conversionRate,
            isFavourite = false,
            isFeatured = false,
        )
        currencyDao.insert(currencyEntity)
    }

    fun getCurrency(currencyCode: String): Flow<Currency?> {
        return currencyDao.getCurrencyByIdFlow(currencyCode)
            .map { currencyEntity ->
                Currency(
                    currencyEntity?.currencyCode ?: return@map null,
                    currencyEntity.conversionRate
                )
            }
    }
}

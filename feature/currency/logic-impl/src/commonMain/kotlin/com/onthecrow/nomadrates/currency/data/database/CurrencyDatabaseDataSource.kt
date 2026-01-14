package com.onthecrow.nomadrates.currency.data.database

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
                currencies.map { currency ->
                    Currency(
                        code = currency.currencyCode,
                        conversionRate = currency.conversionRate,
                        isFavourite = currency.isFavourite,
                        isFeatured = currency.isFeatured,
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
                isFavourite = currency.isFavourite,
                isFeatured = currency.isFeatured,
            )
        }
        currencyDao.insertAll(currencyEntities)
    }

    suspend fun saveCurrency(currency: Currency) {
        val currencyEntity = CurrencyEntity(
            id = currency.code,
            currencyCode = currency.code,
            conversionRate = currency.conversionRate,
            isFavourite = currency.isFavourite,
            isFeatured = currency.isFeatured,
        )
        currencyDao.insert(currencyEntity)
    }

    fun getCurrencyFlow(currencyCode: String): Flow<Currency?> {
        return currencyDao.getCurrencyByIdFlow(currencyCode)
            .map { currencyEntity ->
                Currency(
                    code = currencyEntity?.currencyCode ?: return@map null,
                    conversionRate = currencyEntity.conversionRate,
                    isFavourite = currencyEntity.isFavourite,
                    isFeatured = currencyEntity.isFeatured,
                )
            }
    }

    suspend fun getCurrency(currencyCode: String): Currency? {
        val currency = currencyDao.getCurrencyById(currencyCode)
        return Currency(
            code = currency?.currencyCode ?: return null,
            conversionRate = currency.conversionRate,
            isFavourite = currency.isFavourite,
            isFeatured = currency.isFeatured,
        )
    }
}

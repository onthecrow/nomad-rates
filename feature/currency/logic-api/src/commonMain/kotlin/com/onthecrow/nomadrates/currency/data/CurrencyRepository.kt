package com.onthecrow.nomadrates.currency.data

import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getCurrencyList(): Flow<List<Currency>?>
    suspend fun getCurrency(currencyCode: String): Currency?
    fun getCurrencyFlow(currencyCode: String): Flow<Currency?>
    fun getBaseCurrency(): Flow<Currency?>
    suspend fun saveCurrency(currency: Currency)
}

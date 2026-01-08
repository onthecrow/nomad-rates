package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow

interface GetCurrencyListUseCase {
    operator fun invoke(): Flow<List<Currency>?>
}

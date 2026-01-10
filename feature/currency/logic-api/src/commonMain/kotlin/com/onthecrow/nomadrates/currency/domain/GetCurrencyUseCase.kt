package com.onthecrow.nomadrates.currency.domain

import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow

interface GetCurrencyUseCase {
    operator fun invoke(currencyCode: String): Flow<Currency?>
}

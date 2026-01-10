package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow

internal class GetCurrencyUseCaseImpl(
    private val currencyRepository: CurrencyRepository
) : GetCurrencyUseCase {
    override fun invoke(currencyCode: String): Flow<Currency?> {
        return currencyRepository.getCurrency(currencyCode)
    }
}

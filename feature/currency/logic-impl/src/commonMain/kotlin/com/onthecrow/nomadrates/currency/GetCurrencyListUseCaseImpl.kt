package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.model.Currency
import kotlinx.coroutines.flow.Flow

internal class GetCurrencyListUseCaseImpl(
    private val currencyRepository: CurrencyRepository
) : GetCurrencyListUseCase {
    override fun invoke(): Flow<List<Currency>?> {
        return currencyRepository.getCurrencyList()
    }
}

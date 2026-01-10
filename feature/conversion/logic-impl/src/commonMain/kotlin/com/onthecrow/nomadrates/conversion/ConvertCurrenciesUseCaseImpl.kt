package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import kotlinx.coroutines.flow.first

class ConvertCurrenciesUseCaseImpl(
    private val currencyRepository: CurrencyRepository
): ConvertCurrenciesUseCase {
    override suspend fun invoke(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: Double
    ): Double {
        // TODO implement error handling
        val base = currencyRepository.getBaseCurrency().first() ?: return amount
        val from = currencyRepository.getCurrency(fromCurrencyCode).first() ?: return amount
        val to = currencyRepository.getCurrency(toCurrencyCode).first() ?: return amount

        val inBaseCurrency = amount / from.conversionRate
        return inBaseCurrency * to.conversionRate
    }
}

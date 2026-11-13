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
    ): Double? {
        // Only the saved rates are used, like everywhere else on the screen: the fresh remote
        // response is missing while rates are being fetched and when the fetch fails.
        val from = currencyRepository.getCurrencyFlow(fromCurrencyCode).first() ?: return null
        val to = currencyRepository.getCurrencyFlow(toCurrencyCode).first() ?: return null

        return amount / from.conversionRate * to.conversionRate
    }
}

package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetHistoricalRatesUseCaseImpl(
    private val currencyRepository: CurrencyRepository,
): GetHistoricalRatesUseCase {
    override operator fun invoke(fromCurrencyCode: String, toCurrencyCode: String): Flow<List<Double>?> {
        return combine(
            currencyRepository.getCurrencyFlow(fromCurrencyCode),
            currencyRepository.getCurrencyFlow(toCurrencyCode),
        ) { fromCurrency, toCurrency ->
            val fromRates = fromCurrency?.rates ?: return@combine null
            val toRates = toCurrency?.rates ?: return@combine null
            fromRates.mapIndexed { index, fromRate -> toRates[index] / fromRate }
        }
    }
}

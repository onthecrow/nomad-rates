package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionRepository
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

internal class GetConversionPairUseCaseImpl(
    private val getHistoricalRatesUseCase: GetHistoricalRatesUseCase,
    private val conversionRepository: ConversionRepository,
    private val currencyRepository: CurrencyRepository,
): GetConversionPairUseCase {
    override fun invoke(fromCurrencyCode: String, toCurrencyCode: String): Flow<ConversionPair?> {
        return combine(
            conversionRepository.getConversionPairsFlow(),
            currencyRepository.getCurrencyList(),
        ) { conversionPairs, currencies ->
            val conversionPairLocal = conversionPairs.find { conversionPair ->
                conversionPair.fromCurrencyCode == fromCurrencyCode && conversionPair.toCurrencyCode == toCurrencyCode
            }
            val fromCurrency = currencies?.find { currency -> currency.code == fromCurrencyCode } ?: return@combine null
            val toCurrency = currencies.find { currency -> currency.code == toCurrencyCode } ?: return@combine null
            val historicalRates = getHistoricalRatesUseCase(fromCurrency.code, toCurrency.code).first()

            ConversionPair(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                conversionRate = (toCurrency.conversionRate / fromCurrency.conversionRate),
                historicalRates = historicalRates ?: emptyList(),
                isFeatured = conversionPairLocal?.isFeatured ?: false,
                isFavourite = conversionPairLocal?.isFavorite ?: false,
            )
        }
    }
}

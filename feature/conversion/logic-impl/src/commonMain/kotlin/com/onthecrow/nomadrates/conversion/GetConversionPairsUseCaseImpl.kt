package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionRepository
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

internal class GetConversionPairsUseCaseImpl(
    private val conversionRepository: ConversionRepository,
    private val currencyRepository: CurrencyRepository,
    private val getHistoricalRatesUseCase: GetHistoricalRatesUseCase,
): GetConversionPairsUseCase {
    override operator fun invoke(): Flow<List<ConversionPair>> {
        return combine(
            conversionRepository.getConversionPairsFlow(),
            currencyRepository.getCurrencyList(),
        ) { conversionPairs, currencies ->
            conversionPairs.mapNotNull { conversionPair ->
                val fromCurrency = currencies?.find { currency -> currency.code == conversionPair.fromCurrencyCode } ?: return@mapNotNull null
                val toCurrency = currencies.find { currency -> currency.code == conversionPair.toCurrencyCode } ?: return@mapNotNull null
                val historicalRates = getHistoricalRatesUseCase(fromCurrency.code, toCurrency.code).first()
                ConversionPair(
                    fromCurrency = fromCurrency,
                    toCurrency = toCurrency,
                    conversionRate = (toCurrency.conversionRate / fromCurrency.conversionRate),
                    historicalRates = historicalRates ?: emptyList(),
                    isFeatured = conversionPair.isFeatured,
                    isFavourite = conversionPair.isFavorite,
                )
            }
        }
    }
}

package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionRepository
import com.onthecrow.nomadrates.conversion.data.database.ConversionEntity
import com.onthecrow.nomadrates.conversion.domain.ToggleConversionPairFavouriteUseCase
import kotlinx.coroutines.flow.first

internal class ToggleConversionPairFavouriteUseCaseImpl(
    private val conversionRepository: ConversionRepository,
) : ToggleConversionPairFavouriteUseCase {
    override suspend fun invoke(currencyFromCode: String, currencyToCode: String) {
        val currenciesLocal = conversionRepository.getConversionPairsFlow().first()
        val conversionEntity = currenciesLocal.find { conversionPair ->
            conversionPair.fromCurrencyCode == currencyFromCode &&
                    conversionPair.toCurrencyCode == currencyToCode
        }
            ?.let { conversionEntity ->
                conversionEntity.copy(isFavorite = !conversionEntity.isFavorite)
            }
            ?: ConversionEntity(
                fromCurrencyCode = currencyFromCode,
                toCurrencyCode = currencyToCode,
                isFeatured = false,
                isFavorite = true,
            )

        conversionRepository.saveConversion(conversionEntity)
    }
}

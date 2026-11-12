package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.SetFavouritesConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide
import kotlinx.coroutines.flow.first

internal class SetFavouritesConversionBaseCurrencyUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val saveSelectedConversionPairUseCase: SaveSelectedConversionPairUseCase,
) : SetFavouritesConversionBaseCurrencyUseCase {
    override suspend fun invoke(currencyCode: String, side: ConversionPairSide) {
        val selectedConversionPair = getSelectedConversionPairUseCase().first()
        val updatedPair = when (side) {
            ConversionPairSide.FROM -> selectedConversionPair.copy(fromCurrencyCode = currencyCode)
            ConversionPairSide.TO -> selectedConversionPair.copy(toCurrencyCode = currencyCode)
        }
        if (updatedPair == selectedConversionPair) return

        saveSelectedConversionPairUseCase(updatedPair)
    }
}

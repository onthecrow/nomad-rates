package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.favouritesconversion.domain.SetFavouritesConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import kotlinx.coroutines.flow.first

internal class SetFavouritesConversionBaseCurrencyUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val saveSelectedConversionPairUseCase: SaveSelectedConversionPairUseCase,
) : SetFavouritesConversionBaseCurrencyUseCase {
    override suspend fun invoke(currencyCode: String) {
        val selectedConversionPair = getSelectedConversionPairUseCase().first()
        if (selectedConversionPair.fromCurrencyCode == currencyCode) return

        saveSelectedConversionPairUseCase(
            selectedConversionPair.copy(fromCurrencyCode = currencyCode)
        )
    }
}

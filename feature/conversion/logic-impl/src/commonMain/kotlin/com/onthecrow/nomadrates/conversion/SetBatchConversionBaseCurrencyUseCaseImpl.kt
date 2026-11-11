package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.domain.SetBatchConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair

internal class SetBatchConversionBaseCurrencyUseCaseImpl(
    private val conversionSelectionRepository: ConversionSelectionRepository,
) : SetBatchConversionBaseCurrencyUseCase {
    override suspend fun invoke(currencyCode: String) {
        val selectedConversionPair = conversionSelectionRepository.getCurrentSelectedConversionPairOrNull()
            ?: SelectedConversionPair.DEFAULT
        if (selectedConversionPair.fromCurrencyCode == currencyCode) return

        conversionSelectionRepository.saveSelectedConversionPair(
            selectedConversionPair.copy(fromCurrencyCode = currencyCode)
        )
    }
}

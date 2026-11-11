package com.onthecrow.nomadrates.batch

import com.onthecrow.nomadrates.batch.domain.SetBatchConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import kotlinx.coroutines.flow.first

internal class SetBatchConversionBaseCurrencyUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val saveSelectedConversionPairUseCase: SaveSelectedConversionPairUseCase,
) : SetBatchConversionBaseCurrencyUseCase {
    override suspend fun invoke(currencyCode: String) {
        val selectedConversionPair = getSelectedConversionPairUseCase().first()
        if (selectedConversionPair.fromCurrencyCode == currencyCode) return

        saveSelectedConversionPairUseCase(
            selectedConversionPair.copy(fromCurrencyCode = currencyCode)
        )
    }
}

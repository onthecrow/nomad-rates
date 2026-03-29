package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair

internal class SaveSelectedConversionPairUseCaseImpl(
    private val conversionSelectionRepository: ConversionSelectionRepository,
) : SaveSelectedConversionPairUseCase {
    override suspend fun invoke(selectedConversionPair: SelectedConversionPair) {
        conversionSelectionRepository.saveSelectedConversionPair(selectedConversionPair)
    }
}

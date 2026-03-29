package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class GetSelectedConversionPairUseCaseImpl(
    private val conversionSelectionRepository: ConversionSelectionRepository,
) : GetSelectedConversionPairUseCase {
    override fun invoke(): Flow<SelectedConversionPair> =
        conversionSelectionRepository.getSelectedConversionPair()
            .map { it ?: SelectedConversionPair.DEFAULT }
            .distinctUntilChanged()
}

package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import kotlinx.coroutines.flow.Flow

interface GetConversionPairUseCase {
    operator fun invoke(fromCurrencyCode: String, toCurrencyCode: String): Flow<ConversionPair?>
}

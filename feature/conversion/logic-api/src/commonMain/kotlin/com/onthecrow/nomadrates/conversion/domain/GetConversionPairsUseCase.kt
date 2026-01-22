package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import kotlinx.coroutines.flow.Flow

interface GetConversionPairsUseCase {
    operator fun invoke(): Flow<List<ConversionPair>>
}

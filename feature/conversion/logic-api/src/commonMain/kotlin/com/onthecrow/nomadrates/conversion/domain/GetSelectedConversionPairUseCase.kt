package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow

interface GetSelectedConversionPairUseCase {
    operator fun invoke(): Flow<SelectedConversionPair>
}

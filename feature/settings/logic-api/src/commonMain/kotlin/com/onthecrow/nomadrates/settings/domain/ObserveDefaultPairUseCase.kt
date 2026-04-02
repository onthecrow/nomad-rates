package com.onthecrow.nomadrates.settings.domain

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow

interface ObserveDefaultPairUseCase {
    operator fun invoke(): Flow<SelectedConversionPair>
}

package com.onthecrow.nomadrates.conversion.domain

import kotlinx.coroutines.flow.Flow

interface ObserveConversionDataUseCase {
    operator fun invoke(): Flow<ConversionDataState>
}

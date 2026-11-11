package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.BatchConversion
import kotlinx.coroutines.flow.Flow

interface ObserveBatchConversionUseCase {
    operator fun invoke(): Flow<BatchConversion>
}

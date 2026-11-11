package com.onthecrow.nomadrates.batch.domain

import com.onthecrow.nomadrates.batch.domain.model.BatchConversion
import kotlinx.coroutines.flow.Flow

interface ObserveBatchConversionUseCase {
    operator fun invoke(): Flow<BatchConversion>
}

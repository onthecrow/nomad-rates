package com.onthecrow.nomadrates.currency.domain

import kotlinx.coroutines.flow.Flow

interface ObserveLastRatesTimestampUseCase {
    operator fun invoke(): Flow<Long?>
}

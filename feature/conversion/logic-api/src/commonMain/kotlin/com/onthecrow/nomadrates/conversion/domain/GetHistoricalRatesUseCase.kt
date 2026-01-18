package com.onthecrow.nomadrates.conversion.domain

import kotlinx.coroutines.flow.Flow

interface GetHistoricalRatesUseCase {
    operator fun invoke(fromCurrencyCode: String, toCurrencyCode: String): Flow<List<Double>?>
}

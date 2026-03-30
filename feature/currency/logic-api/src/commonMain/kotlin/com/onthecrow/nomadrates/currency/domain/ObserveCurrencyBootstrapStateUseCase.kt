package com.onthecrow.nomadrates.currency.domain

import kotlinx.coroutines.flow.Flow

interface ObserveCurrencyBootstrapStateUseCase {
    operator fun invoke(): Flow<CurrencyBootstrapState>
}

package com.onthecrow.nomadrates.settings.domain

import kotlinx.coroutines.flow.Flow

interface ObserveShowFeaturedCurrenciesUseCase {
    operator fun invoke(): Flow<Boolean>
}

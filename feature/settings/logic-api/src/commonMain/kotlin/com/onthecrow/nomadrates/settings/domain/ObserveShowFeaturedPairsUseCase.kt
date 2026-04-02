package com.onthecrow.nomadrates.settings.domain

import kotlinx.coroutines.flow.Flow

interface ObserveShowFeaturedPairsUseCase {
    operator fun invoke(): Flow<Boolean>
}

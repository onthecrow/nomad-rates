package com.onthecrow.nomadrates.settings.domain

import kotlinx.coroutines.flow.Flow

interface ObserveUseEnglishCurrencyNamesUseCase {
    operator fun invoke(): Flow<Boolean>
}

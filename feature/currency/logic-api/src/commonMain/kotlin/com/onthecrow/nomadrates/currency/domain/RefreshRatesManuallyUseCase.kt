package com.onthecrow.nomadrates.currency.domain

interface RefreshRatesManuallyUseCase {
    suspend operator fun invoke()
}

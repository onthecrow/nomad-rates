package com.onthecrow.nomadrates.currency.domain

interface ToggleCurrencyFavoriteUseCase {
    suspend operator fun invoke(currencyCode: String)
}

package com.onthecrow.nomadrates.currency.domain

/**
 * Marks the currency as a favourite. Unlike [ToggleCurrencyFavoriteUseCase], an already favourite
 * currency stays favourite.
 */
interface AddCurrencyToFavoritesUseCase {
    suspend operator fun invoke(currencyCode: String)
}

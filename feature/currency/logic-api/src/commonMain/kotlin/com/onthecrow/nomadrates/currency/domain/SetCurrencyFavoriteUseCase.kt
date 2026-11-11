package com.onthecrow.nomadrates.currency.domain

/**
 * Adds the currency to favourites or removes it from them. Unlike [ToggleCurrencyFavoriteUseCase],
 * repeating the call does not flip the state back.
 */
interface SetCurrencyFavoriteUseCase {
    suspend operator fun invoke(currencyCode: String, isFavourite: Boolean)
}

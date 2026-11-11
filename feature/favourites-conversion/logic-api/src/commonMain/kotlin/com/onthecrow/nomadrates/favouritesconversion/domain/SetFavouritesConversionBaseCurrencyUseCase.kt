package com.onthecrow.nomadrates.favouritesconversion.domain

/**
 * Changes the base currency of the favourites conversion. The base currency is the "from" currency of
 * the selected conversion pair, so the main conversion screen picks the change up as well.
 */
interface SetFavouritesConversionBaseCurrencyUseCase {
    suspend operator fun invoke(currencyCode: String)
}

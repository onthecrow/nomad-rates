package com.onthecrow.nomadrates.favouritesconversion.domain

import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide

/**
 * Changes the base currency of the favourites conversion. The base currency is the [side]
 * currency of the selected conversion pair, so the conversion screen picks the change up as well.
 */
interface SetFavouritesConversionBaseCurrencyUseCase {
    suspend operator fun invoke(currencyCode: String, side: ConversionPairSide)
}

package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * @param baseCurrencyCode currency shown in the amount field until the data is loaded; blank if unknown.
 * @param amount amount to start with, as raw input like "1234.5"; blank means the default amount.
 * @param side the field of the conversion screen the screen is opened from; its currency is the base.
 */
@Serializable
data class FavouritesConversionDestination(
    val baseCurrencyCode: String = "",
    val amount: String = "",
    val side: FavouritesConversionSide = FavouritesConversionSide.FROM,
) : Destination

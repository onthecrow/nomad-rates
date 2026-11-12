package com.onthecrow.nomadrates.favouritesconversion

import kotlinx.serialization.Serializable

/**
 * Which field of the conversion screen the favourites conversion is opened from and gives its
 * amount back to: the "from" (top) or the "to" (bottom) one.
 */
@Serializable
enum class FavouritesConversionSide {
    FROM,
    TO,
}

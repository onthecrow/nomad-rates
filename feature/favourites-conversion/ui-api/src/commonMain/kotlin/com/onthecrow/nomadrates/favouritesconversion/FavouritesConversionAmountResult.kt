package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.navigation.ScreenResult

/**
 * Sent while the amount on the favourites conversion screen changes, so the field of the
 * conversion screen it was opened from keeps the same amount.
 *
 * @param amount raw input like "1234.5".
 * @param side the field of the conversion screen the amount belongs to.
 */
data class FavouritesConversionAmountResult(
    val amount: String,
    val side: FavouritesConversionSide,
) : ScreenResult

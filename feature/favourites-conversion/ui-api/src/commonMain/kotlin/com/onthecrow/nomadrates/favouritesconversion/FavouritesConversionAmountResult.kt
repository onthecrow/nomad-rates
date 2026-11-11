package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.navigation.ScreenResult

/**
 * Sent while the amount on the favourites conversion screen changes, so the "from" field of the
 * conversion screen keeps the same amount.
 *
 * @param amount raw input like "1234.5".
 */
data class FavouritesConversionAmountResult(
    val amount: String,
) : ScreenResult

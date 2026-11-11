package com.onthecrow.nomadrates.favouritesconversion.model

internal data class FavouritesConversionListItem(
    val flagIcon: String,
    val currencyCode: String,
    val currencyName: String,
    /** Units of this currency per one unit of the base currency. */
    val conversionRate: Double,
    val convertedAmount: String,
) {
    val listKey: String = "favourites_$currencyCode"
}

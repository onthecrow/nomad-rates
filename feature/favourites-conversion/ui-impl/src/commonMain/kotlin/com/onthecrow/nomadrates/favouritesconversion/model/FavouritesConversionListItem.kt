package com.onthecrow.nomadrates.favouritesconversion.model

internal data class FavouritesConversionListItem(
    val flagIcon: String,
    val currencyCode: String,
    val currencyName: String,
    /** Units of this currency per one unit of the base currency. */
    val conversionRate: Double,
    val convertedAmount: String,
    /** A shorter form of [convertedAmount] for when it does not fit; null for amounts that always fit. */
    val compactAmount: CompactAmount? = null,
) {
    val listKey: String = "favourites_$currencyCode"
}

/**
 * An amount in scientific notation: [mantissa] × 10^[exponent], e.g. "2,16" and 21 for
 * 2 160 864 034 639 659 177 530.
 */
internal data class CompactAmount(
    val mantissa: String,
    val exponent: Int,
)

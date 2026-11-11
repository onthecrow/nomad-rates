package com.onthecrow.nomadrates.favouritesconversion.domain.model

import com.onthecrow.nomadrates.currency.model.Currency

/**
 * One amount in [baseCurrency] converted into every favourite currency.
 */
data class FavouritesConversion(
    val baseCurrency: Currency,
    val targets: List<FavouritesConversionTarget>,
)

data class FavouritesConversionTarget(
    val currency: Currency,
    /** How many units of [currency] one unit of the base currency buys. */
    val conversionRate: Double,
)

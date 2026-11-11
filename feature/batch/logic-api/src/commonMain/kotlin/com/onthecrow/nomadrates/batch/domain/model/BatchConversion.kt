package com.onthecrow.nomadrates.batch.domain.model

import com.onthecrow.nomadrates.currency.model.Currency

/**
 * One amount in [baseCurrency] converted into every favourite currency.
 */
data class BatchConversion(
    val baseCurrency: Currency,
    val targets: List<BatchConversionTarget>,
)

data class BatchConversionTarget(
    val currency: Currency,
    /** How many units of [currency] one unit of the base currency buys. */
    val conversionRate: Double,
)

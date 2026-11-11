package com.onthecrow.nomadrates.batch.model

internal data class BatchConversionListItem(
    val flagIcon: String,
    val currencyCode: String,
    val currencyName: String,
    /** Units of this currency per one unit of the base currency. */
    val conversionRate: Double,
    val convertedAmount: String,
) {
    val listKey: String = "batch_$currencyCode"
}

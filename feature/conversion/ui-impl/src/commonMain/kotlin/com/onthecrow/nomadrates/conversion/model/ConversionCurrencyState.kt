package com.onthecrow.nomadrates.conversion.model

internal data class ConversionCurrencyState(
    val currencyIcon: String,
    val currencyCode: String,
    val conversionRate: Double,
    val conversionValue: String,
)

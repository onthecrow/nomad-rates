package com.onthecrow.nomadrates.conversion.domain.model

import com.onthecrow.nomadrates.currency.model.Currency

data class ConversionPair(
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val conversionRate: Double,
    val historicalRates: List<Double>,
    val isFeatured: Boolean,
    val isFavourite: Boolean,
)

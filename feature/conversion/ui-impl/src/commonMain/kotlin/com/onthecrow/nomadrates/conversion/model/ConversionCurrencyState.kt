package com.onthecrow.nomadrates.conversion.model

internal sealed interface ConversionCurrencyViewState {
    data object Loading : ConversionCurrencyViewState

    data class Content(
        val currencyIcon: String,
        val currencyCode: String,
        val conversionRate: Double,
        val conversionValue: String,
    ) : ConversionCurrencyViewState
}

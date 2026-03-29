package com.onthecrow.nomadrates.conversion.domain.model

data class SelectedConversionPair(
    val fromCurrencyCode: String,
    val toCurrencyCode: String,
) {
    companion object {
        val DEFAULT: SelectedConversionPair =
            SelectedConversionPair(fromCurrencyCode = "USD", toCurrencyCode = "EUR")
    }
}

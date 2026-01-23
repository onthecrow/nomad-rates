package com.onthecrow.nomadrates.conversion.model

internal data class ConversionViewState(
    val from: ConversionCurrencyState? = null,
    val to: ConversionCurrencyState? = null,
    val isFavourite: Boolean = false,
)

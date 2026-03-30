package com.onthecrow.nomadrates.conversion.model

internal sealed interface ConversionViewState {
    data object Loading : ConversionViewState

    data class Content(
        val from: ConversionCurrencyViewState.Content,
        val to: ConversionCurrencyViewState.Content,
        val isFavourite: Boolean = false,
    ) : ConversionViewState
}

package com.onthecrow.nomadrates.conversion.model

import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState

internal sealed interface ConversionViewState {
    data object Loading : ConversionViewState

    data class Content(
        val from: CurrencyAmountViewState.Content,
        val to: CurrencyAmountViewState.Content,
        val isFavourite: Boolean = false,
    ) : ConversionViewState
}

package com.onthecrow.nomadrates.conversion.model

import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState

internal sealed interface ConversionViewState {
    data object Loading : ConversionViewState

    data class Content(
        val from: CurrencyAmountViewState.Content,
        val to: CurrencyAmountViewState.Content,
        val isFavourite: Boolean = false,
        /**
         * How many times the fields were swapped. The view keeps each field as the same element
         * and moves it to the other place on every swap, so the fields visibly change places.
         */
        val swapCount: Int = 0,
    ) : ConversionViewState
}

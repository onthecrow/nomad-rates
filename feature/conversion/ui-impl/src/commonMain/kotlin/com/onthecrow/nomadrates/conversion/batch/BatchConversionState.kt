package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.conversion.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyViewState
import com.onthecrow.nomadrates.uicore.State

internal sealed interface BatchConversionState : State {
    data object Loading : BatchConversionState

    data class Content(
        /** The amount input; its [ConversionCurrencyViewState.Content.conversionValue] is the entered amount. */
        val baseCurrency: ConversionCurrencyViewState.Content,
        val favourites: List<BatchConversionListItem>,
    ) : BatchConversionState
}

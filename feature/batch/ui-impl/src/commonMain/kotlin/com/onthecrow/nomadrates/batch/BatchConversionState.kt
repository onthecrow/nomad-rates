package com.onthecrow.nomadrates.batch

import com.onthecrow.nomadrates.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.uicore.State

internal sealed interface BatchConversionState : State {
    /** The entered amount as raw input like "1234.5". */
    val amount: String

    /** State of the amount field at the bottom of the screen. */
    val amountField: CurrencyAmountViewState

    data class Loading(
        override val amount: String = BatchConversionReducer.DEFAULT_AMOUNT,
        /**
         * Currency the screen was opened with. The amount field shows it before the data arrives,
         * so the field looks the same as on the conversion screen during the shared transition.
         */
        val baseCurrencyCode: String? = null,
    ) : BatchConversionState {
        override val amountField: CurrencyAmountViewState
            get() = baseCurrencyCode
                ?.let { currencyCode ->
                    CurrencyAmountViewState.Content.forCurrency(
                        currencyCode = currencyCode,
                        conversionValue = amount,
                    )
                }
                ?: CurrencyAmountViewState.Loading
    }

    data class Content(
        /** The amount input; its [CurrencyAmountViewState.Content.conversionValue] is the entered amount. */
        val baseCurrency: CurrencyAmountViewState.Content,
        val favourites: List<BatchConversionListItem>,
    ) : BatchConversionState {
        override val amount: String
            get() = baseCurrency.conversionValue

        override val amountField: CurrencyAmountViewState
            get() = baseCurrency
    }
}

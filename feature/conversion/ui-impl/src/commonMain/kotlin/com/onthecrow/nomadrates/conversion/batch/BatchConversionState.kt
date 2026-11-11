package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.conversion.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyViewState
import com.onthecrow.nomadrates.uicore.State

internal sealed interface BatchConversionState : State {
    /** The entered amount as raw input like "1234.5". */
    val amount: String

    /** State of the amount field at the bottom of the screen. */
    val amountField: ConversionCurrencyViewState

    data class Loading(
        override val amount: String = BatchConversionReducer.DEFAULT_AMOUNT,
        /**
         * Currency the screen was opened with. The amount field shows it before the data arrives,
         * so the field looks the same as on the conversion screen during the shared transition.
         */
        val baseCurrencyCode: String? = null,
    ) : BatchConversionState {
        override val amountField: ConversionCurrencyViewState
            get() = baseCurrencyCode
                ?.let { currencyCode ->
                    ConversionCurrencyStateMapper.fromCurrencyCode(
                        currencyCode = currencyCode,
                        conversionRate = UNKNOWN_CONVERSION_RATE,
                        conversionValue = amount,
                    )
                }
                ?: ConversionCurrencyViewState.Loading
    }

    data class Content(
        /** The amount input; its [ConversionCurrencyViewState.Content.conversionValue] is the entered amount. */
        val baseCurrency: ConversionCurrencyViewState.Content,
        val favourites: List<BatchConversionListItem>,
    ) : BatchConversionState {
        override val amount: String
            get() = baseCurrency.conversionValue

        override val amountField: ConversionCurrencyViewState
            get() = baseCurrency
    }

    private companion object {
        /** The amount field does not show the rate, and it is not known before the data arrives. */
        const val UNKNOWN_CONVERSION_RATE = 0.0
    }
}

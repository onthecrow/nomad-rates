package com.onthecrow.nomadrates.ui.model

import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toFlagAssetKey

/**
 * State of [com.onthecrow.nomadrates.ui.view.CurrencyAmountView]: a currency selector with an amount input.
 */
sealed interface CurrencyAmountViewState {
    data object Loading : CurrencyAmountViewState

    data class Content(
        val currencyIcon: String,
        val currencyCode: String,
        /** The amount as raw input like "1234.5". */
        val conversionValue: String,
    ) : CurrencyAmountViewState {
        companion object {
            fun forCurrency(
                currencyCode: String,
                conversionValue: String,
            ): Content = Content(
                currencyIcon = currencyCode.toFlagAssetKey().toFlagResourceUri(),
                currencyCode = currencyCode,
                conversionValue = conversionValue,
            )
        }
    }
}

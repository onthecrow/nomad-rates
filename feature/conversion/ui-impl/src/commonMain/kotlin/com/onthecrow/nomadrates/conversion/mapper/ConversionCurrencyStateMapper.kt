package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyViewState
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toFlagAssetKey

internal object ConversionCurrencyStateMapper {
    fun fromCurrency(
        currency: Currency,
        conversionValue: String,
    ): ConversionCurrencyViewState.Content {
        return ConversionCurrencyViewState.Content(
            currencyIcon = currency.code.toFlagAssetKey().toFlagResourceUri(),
            currencyCode = currency.code,
            conversionRate = currency.conversionRate,
            conversionValue = conversionValue,
        )
    }
}

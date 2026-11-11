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
        return fromCurrencyCode(
            currencyCode = currency.code,
            conversionRate = currency.conversionRate,
            conversionValue = conversionValue,
        )
    }

    fun fromCurrencyCode(
        currencyCode: String,
        conversionRate: Double,
        conversionValue: String,
    ): ConversionCurrencyViewState.Content {
        return ConversionCurrencyViewState.Content(
            currencyIcon = currencyCode.toFlagAssetKey().toFlagResourceUri(),
            currencyCode = currencyCode,
            conversionRate = conversionRate,
            conversionValue = conversionValue,
        )
    }
}

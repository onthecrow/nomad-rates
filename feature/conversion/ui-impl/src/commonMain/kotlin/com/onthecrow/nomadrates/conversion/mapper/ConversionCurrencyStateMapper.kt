package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState

internal object ConversionCurrencyStateMapper {
    fun fromCurrency(
        currency: Currency,
        conversionValue: String,
    ): CurrencyAmountViewState.Content {
        return CurrencyAmountViewState.Content.forCurrency(
            currencyCode = currency.code,
            conversionValue = conversionValue,
        )
    }
}

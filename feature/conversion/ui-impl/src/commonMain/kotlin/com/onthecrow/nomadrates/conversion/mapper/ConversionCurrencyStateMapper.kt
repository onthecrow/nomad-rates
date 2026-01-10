package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyState
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toIsoCountryCode

internal object ConversionCurrencyStateMapper {
    fun fromCurrency(currency: Currency, conversionValue: String): ConversionCurrencyState {
        return ConversionCurrencyState(
            currencyIcon = currency.code.toIsoCountryCode().toFlagResourceUri(),
            currencyCode = currency.code,
            conversionRate = currency.conversionRate,
            conversionValue = conversionValue,
        )
    }
}

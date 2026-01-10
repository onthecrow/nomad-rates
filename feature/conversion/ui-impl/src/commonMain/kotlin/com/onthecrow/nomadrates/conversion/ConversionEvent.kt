package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface ConversionEvent: Event {
    data object OnBackPress : ConversionEvent
    data object OnFromCurrencyClick : ConversionEvent
    data object OnToCurrencyClick : ConversionEvent
    data class OnFromValueConverted(val newValue: String) : ConversionEvent
    data class OnFromValueChange(val value: String) : ConversionEvent
    data class OnToValueConverted(val newValue: String) : ConversionEvent
    data class OnToValueChange(val value: String) : ConversionEvent
    data class OnFromCurrencyChange(val currency: Currency) : ConversionEvent
    data class OnToCurrencyChange(val currency: Currency) : ConversionEvent
    data object OnSwitchButtonPress : ConversionEvent
}

package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface ConversionEvent : Event {
    data object OnBackPress : ConversionEvent
    data object OnFromCurrencyClick : ConversionEvent
    data object OnToCurrencyClick : ConversionEvent
    data object OnSwitchButtonPress : ConversionEvent
    data object OnActiveConversionPairFavouritesClick : ConversionEvent
    data class OnFromValueConverted(val newValue: String) : ConversionEvent
    data class OnFromValueChange(val value: String) : ConversionEvent
    data class OnToValueConverted(val newValue: String) : ConversionEvent
    data class OnToValueChange(val value: String) : ConversionEvent
    data class OnConversionPairsReceived(val conversionPairs: List<ConversionPair>) : ConversionEvent
    data class OnConversionViewClick(val conversionPair: Pair<String, String>) : ConversionEvent
    data class OnActiveConversionPairChanged(val conversionPair: ConversionPair) : ConversionEvent
    data class OnConversionPairFavouritesClick(
        // todo replace with a class or proper id
        val currencyPair: Pair<String, String>,
    ) : ConversionEvent
}

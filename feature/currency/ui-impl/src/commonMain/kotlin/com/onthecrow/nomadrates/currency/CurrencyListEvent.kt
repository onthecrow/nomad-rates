package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface CurrencyListEvent: Event {
    data object OnBackPress : CurrencyListEvent
    data object OnSearchValueClear : CurrencyListEvent
    data object OnScrollToTop : CurrencyListEvent
    data class OnAddToFavouriteClick(val currencyCode: String) : CurrencyListEvent
    data class OnCurrencyClick(val currencyCode: String) : CurrencyListEvent
    data class OnCurrencyListUpdate(
        val currencies: List<Currency>,
        val showFeaturedCurrencies: Boolean,
    ) : CurrencyListEvent
    data class OnSearchValueChange(val value: String) : CurrencyListEvent
}

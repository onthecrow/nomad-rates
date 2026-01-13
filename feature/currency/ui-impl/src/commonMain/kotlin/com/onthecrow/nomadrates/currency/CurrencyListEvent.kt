package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface CurrencyListEvent: Event {
    data object OnBackPress : CurrencyListEvent
    data object OnSearchValueClear : CurrencyListEvent
    data class OnCurrencyClick(val currency: CurrencyListItem.Data) : CurrencyListEvent
    data class OnCurrencyListUpdate(val currencies: List<Currency>) : CurrencyListEvent
    data class OnSearchValueChange(val value: String) : CurrencyListEvent
}

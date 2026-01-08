package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.uicore.Event

sealed interface CurrencyListEvent: Event {
    data class CurrencyListUpdate(val currencies: List<Currency>) : CurrencyListEvent
}

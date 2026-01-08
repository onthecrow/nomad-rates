package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.uicore.Reducer

class CurrencyListReducer : Reducer<CurrencyListState, CurrencyListEvent> {
    override fun reduce(
        state: CurrencyListState,
        event: CurrencyListEvent
    ): CurrencyListState {
        return when (event) {
            is CurrencyListEvent.CurrencyListUpdate -> reduceCurrencyListUpdate(state, event)
            else -> state
        }
    }

    private fun reduceCurrencyListUpdate(
        state: CurrencyListState,
        event: CurrencyListEvent.CurrencyListUpdate
    ): CurrencyListState {
        val mappedCurrencies = event.currencies.map(Currency::toUi)
        return state.copy(currencies = mappedCurrencies)
    }
}

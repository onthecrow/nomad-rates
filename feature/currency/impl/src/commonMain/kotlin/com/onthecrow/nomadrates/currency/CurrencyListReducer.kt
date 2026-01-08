package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.uicore.Reducer

class CurrencyListReducer : Reducer<CurrencyListState, CurrencyListEvent> {
    override fun reduce(
        state: CurrencyListState,
        event: CurrencyListEvent
    ): CurrencyListState {
        return when (event) {
            is CurrencyListEvent.OnCurrencyListUpdate -> reduceCurrencyListUpdate(state, event)
            is CurrencyListEvent.OnSearchValueChange -> reduceOnSearchValueChange(state, event)
            is CurrencyListEvent.OnSearchValueClear -> state.copy(searchValue = "", currenciesFiltered = state.currencies)
            else -> state
        }
    }

    private fun reduceOnSearchValueChange(
        state: CurrencyListState,
        event: CurrencyListEvent.OnSearchValueChange
    ): CurrencyListState {
        return state.copy(searchValue = event.value, currenciesFiltered = state.currencies.filterCurrencies(event.value))
    }

    private fun reduceCurrencyListUpdate(
        state: CurrencyListState,
        event: CurrencyListEvent.OnCurrencyListUpdate
    ): CurrencyListState {
        val mappedCurrencies = event.currencies.map(Currency::toUi)
        return state.copy(
            currencies = mappedCurrencies,
            currenciesFiltered = mappedCurrencies.filterCurrencies(state.searchValue),
        )
    }

    private fun List<CurrencyUI>.filterCurrencies(
        query: String
    ): List<CurrencyUI> {
        return if (query.isEmpty()) {
            this
        } else {
            this.filter { currency ->
                val codeMatch = currency.nameShort.contains(
                    other = query,
                    ignoreCase = true,
                )
                val nameMatch = currency.nameLong.contains(
                    other = query,
                    ignoreCase = true,
                )
                codeMatch || nameMatch
            }
        }
    }
}

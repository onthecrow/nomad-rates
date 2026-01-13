package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.uicore.Reducer

internal class CurrencyListReducer : Reducer<CurrencyListState, CurrencyListEvent> {
    override fun reduce(
        state: CurrencyListState,
        event: CurrencyListEvent
    ): CurrencyListState {
        return when (event) {
            is CurrencyListEvent.OnCurrencyListUpdate -> reduceCurrencyListUpdate(state, event)
            is CurrencyListEvent.OnSearchValueChange -> reduceOnSearchValueChange(state, event)
            is CurrencyListEvent.OnSearchValueClear -> state.copy(
                searchValue = "",
                currenciesFiltered = state.currencies
            )

            else -> state
        }
    }

    private fun reduceOnSearchValueChange(
        state: CurrencyListState,
        event: CurrencyListEvent.OnSearchValueChange
    ): CurrencyListState {
        return state.copy(
            searchValue = event.value,
            currenciesFiltered = state.currencies.filterCurrencies(event.value)
        )
    }

    private fun reduceCurrencyListUpdate(
        state: CurrencyListState,
        event: CurrencyListEvent.OnCurrencyListUpdate
    ): CurrencyListState {
        println("$$$$ pre-mapped: ${event.currencies}")
        val mappedCurrencies = event.currencies.toUi()
        println("$$$$ mapped: $mappedCurrencies")
        return state.copy(
            currencies = mappedCurrencies,
            currenciesFiltered = mappedCurrencies.filterCurrencies(state.searchValue),
        ).also {
            println("$$$$ reduced: ${it.currenciesFiltered}")
        }
    }

    private fun List<CurrencyListItem>.filterCurrencies(
        query: String
    ): List<CurrencyListItem> {
        return if (query.isEmpty()) {
            this
        } else {
            this.filter { currency ->
                if (currency !is CurrencyListItem.Data) return@filter false
                if (currency.isFeatured || currency.isFavourite) return@filter false

                val codeMatch = currency.currencyCode.contains(
                    other = query,
                    ignoreCase = true,
                )
                val nameMatch = currency.currencyName.contains(
                    other = query,
                    ignoreCase = true,
                )
                codeMatch || nameMatch
            }
        }
    }
}

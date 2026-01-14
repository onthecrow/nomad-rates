package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.uicore.Reducer

internal class CurrencyListReducer : Reducer<CurrencyListState, CurrencyListEvent> {
    override suspend fun reduce(
        state: CurrencyListState,
        event: CurrencyListEvent
    ): CurrencyListState {
        return when (event) {
            is CurrencyListEvent.OnCurrencyListUpdate -> reduceCurrencyListUpdate(state, event)
            // TODO implement db/domain search? + currency code search priority
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

    private suspend fun reduceCurrencyListUpdate(
        state: CurrencyListState,
        event: CurrencyListEvent.OnCurrencyListUpdate
    ): CurrencyListState {
        val mappedCurrencies = event.currencies.toUi()
        return state.copy(
            currencies = mappedCurrencies,
            currenciesFiltered = mappedCurrencies.filterCurrencies(state.searchValue),
        )
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

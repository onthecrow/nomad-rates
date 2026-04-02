package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup
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
        val mappedCurrencies = event.currencies.toUi(event.showFeaturedCurrencies)
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
            this.let { currencies ->
                val codeFiltered = currencies.filter { currencyListItem ->
                    if (currencyListItem !is CurrencyListItem.Data) return@filter false
                    if (currencyListItem.listGroup != ListGroup.ALL) return@filter false
                    currencyListItem.currencyCode.contains(
                        other = query,
                        ignoreCase = true,
                    )
                }
                val nameFiltered = currencies.filter { currencyListItem ->
                    if (currencyListItem !is CurrencyListItem.Data) return@filter false
                    if (currencyListItem.listGroup != ListGroup.ALL) return@filter false
                    currencyListItem.currencyName.contains(
                        other = query,
                        ignoreCase = true,
                    )
                }
                return@let codeFiltered.union(nameFiltered).toList()
            }
        }
    }
}

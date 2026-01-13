package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.uicore.State

internal data class CurrencyListState(
    val currencies: List<CurrencyListItem> = emptyList(),
    val currenciesFiltered: List<CurrencyListItem> = emptyList(),
    val searchValue: String = "",
) : State

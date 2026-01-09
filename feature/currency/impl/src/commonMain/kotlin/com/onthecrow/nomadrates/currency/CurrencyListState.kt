package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.uicore.State

internal data class CurrencyListState(
    val currencies: List<CurrencyUI> = emptyList(),
    val currenciesFiltered: List<CurrencyUI> = emptyList(),
    val searchValue: String = "",
) : State

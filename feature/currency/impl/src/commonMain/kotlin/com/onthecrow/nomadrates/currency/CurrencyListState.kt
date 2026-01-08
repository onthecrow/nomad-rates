package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.uicore.State

data class CurrencyListState(
    val currencies: List<CurrencyUI> = emptyList(),
) : State

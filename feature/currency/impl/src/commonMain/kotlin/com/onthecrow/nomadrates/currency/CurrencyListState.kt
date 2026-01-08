package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.model.CurrencyUI

data class CurrencyListState(
    val currencies: List<CurrencyUI> = emptyList(),
)

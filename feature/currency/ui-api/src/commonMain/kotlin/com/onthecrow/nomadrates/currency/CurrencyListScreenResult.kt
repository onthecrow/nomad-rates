package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.navigation.ScreenResult

data class CurrencyListScreenResult(
    val selectedCurrencyCode: String,
    val source: CurrencySelectionSource,
) : ScreenResult

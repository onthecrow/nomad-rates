package com.onthecrow.nomadrates.currency.domain

sealed interface CurrencyBootstrapState {
    data object Cached : CurrencyBootstrapState
    data object Loading : CurrencyBootstrapState
    data object Error : CurrencyBootstrapState
}

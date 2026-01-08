package com.onthecrow.nomadrates.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.mapper.toUi
import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class CurrencyListViewModel(
    getCurrencyListUseCase: GetCurrencyListUseCase
): ViewModel() {

    private val _state = MutableStateFlow(CurrencyListState())
    val state = _state.asStateFlow()

    init {
        getCurrencyListUseCase()
            .map { currencies -> currencies?.map(Currency::toUi) ?: emptyList<CurrencyUI>() }
            .filterNotNull()
            .onEach { currencies -> _state.update { it.copy(currencies = currencies) } }
            .launchIn(viewModelScope)
    }
}

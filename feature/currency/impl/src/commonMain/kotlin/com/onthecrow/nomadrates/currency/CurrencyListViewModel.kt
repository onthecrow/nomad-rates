package com.onthecrow.nomadrates.currency

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class CurrencyListViewModel(
    getCurrencyListUseCase: GetCurrencyListUseCase,
    reducer: CurrencyListReducer,
): BaseViewModel<CurrencyListEvent, CurrencyListState, CurrencyListReducer>(reducer) {

    init {
        getCurrencyListUseCase()
            .filterNotNull()
            .onEach { currencies -> onEvent(CurrencyListEvent.CurrencyListUpdate(currencies)) }
            .launchIn(viewModelScope)
    }

    override fun getInitialState(): CurrencyListState = CurrencyListState()
}

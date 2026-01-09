package com.onthecrow.nomadrates.currency

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class CurrencyListViewModel(
    private val navigator: Navigator,
    getCurrencyListUseCase: GetCurrencyListUseCase,
    reducer: CurrencyListReducer,
): BaseViewModel<CurrencyListEvent, CurrencyListState, CurrencyListReducer>(reducer) {

    init {
        getCurrencyListUseCase()
            .filterNotNull()
            .onEach { currencies ->
                onEvent(CurrencyListEvent.OnCurrencyListUpdate(currencies))
            }
            .launchIn(viewModelScope)
        event.onEach { event ->
            when (event) {
                is CurrencyListEvent.OnBackPress -> onBackPress()
                is CurrencyListEvent.OnCurrencyClick -> onCurrencyClick(event.currency)
                else -> {}
            }
        }
            .launchIn(viewModelScope)
    }

    override fun getInitialState(): CurrencyListState = CurrencyListState()

    private fun onBackPress() {
        navigator.navigateBack()
    }

    private fun onCurrencyClick(currency: CurrencyUI) {
        // TODO return result
        navigator.navigateBack()
    }
}

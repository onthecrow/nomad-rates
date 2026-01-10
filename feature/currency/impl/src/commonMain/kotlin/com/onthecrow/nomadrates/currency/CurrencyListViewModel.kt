package com.onthecrow.nomadrates.currency

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class CurrencyListViewModel(
    private val navigator: Navigator,
    private val screenResultDispatcher: ScreenResultDispatcher,
    getCurrencyListUseCase: GetCurrencyListUseCase,
    reducer: CurrencyListReducer,
) : BaseViewModel<CurrencyListEvent, CurrencyListState, CurrencyListReducer>(reducer) {

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
        screenResultDispatcher.dispatch(
            CurrencyListScreenResult(currency.nameShort)
        )
        navigator.navigateBack()
    }
}

package com.onthecrow.nomadrates.conversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class ConversionViewModel(
    private val navigator: Navigator,
    reducer: ConversionReducer,
): BaseViewModel<ConversionEvent, ConversionState, ConversionReducer>(reducer) {

    init {
        event.onEach { event ->
            when (event) {
                is ConversionEvent.OnBackPress -> onBackPress()
                is ConversionEvent.OnButtonPress -> navigator.navigate(CurrencyListDestination)
                else -> {}
            }
        }
            .launchIn(viewModelScope)
    }

    override fun getInitialState(): ConversionState = ConversionState()

    private fun onBackPress() {
        navigator.navigateBack()
    }
}

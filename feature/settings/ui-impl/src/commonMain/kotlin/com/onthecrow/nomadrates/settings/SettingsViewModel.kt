package com.onthecrow.nomadrates.settings

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class SettingsViewModel(
    private val navigator: Navigator,
    reducer: SettingsReducer,
) : BaseViewModel<SettingsEvent, SettingsState, SettingsReducer>(reducer) {

    init {
        eventFlow.onEach { event ->
            when (event) {
                SettingsEvent.OnBackPress -> navigator.navigateBack()
            }
        }.launchIn(viewModelScope)
    }

    override fun getInitialState(): SettingsState = SettingsState
}

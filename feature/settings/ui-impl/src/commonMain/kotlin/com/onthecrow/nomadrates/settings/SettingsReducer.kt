package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.Reducer

internal class SettingsReducer : Reducer<SettingsState, SettingsEvent> {
    override suspend fun reduce(
        state: SettingsState,
        event: SettingsEvent,
    ): SettingsState = when (event) {
        SettingsEvent.OnBackPress,
        SettingsEvent.OnPrivacyPolicyClick,
        SettingsEvent.OnRefreshClick -> state

        is SettingsEvent.OnAppVersionLoaded -> state.copy(appVersion = event.version)
        is SettingsEvent.OnLastRatesFreshnessChanged -> state.copy(lastRatesFreshness = event.freshness)
        is SettingsEvent.OnLastRatesTimestampChanged -> state.copy(lastRatesTimestamp = event.timestamp)
        is SettingsEvent.OnRefreshStateChanged -> state.copy(isRefreshing = event.isRefreshing)
    }
}

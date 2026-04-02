package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.Reducer

internal class SettingsReducer : Reducer<SettingsState, SettingsEvent> {
    override suspend fun reduce(
        state: SettingsState,
        event: SettingsEvent,
    ): SettingsState = when (event) {
        SettingsEvent.OnBackPress,
        SettingsEvent.OnLaunchPairModeClick,
        SettingsEvent.OnDefaultPairFromClick,
        SettingsEvent.OnDefaultPairToClick,
        SettingsEvent.OnThemeClick,
        SettingsEvent.OnPrivacyPolicyClick,
        SettingsEvent.OnAboutDataSourceClick,
        SettingsEvent.OnRefreshClick -> state

        is SettingsEvent.OnLaunchPairModeSelected,
        is SettingsEvent.OnThemeModeSelected,
        is SettingsEvent.OnShowFeaturedPairsToggle,
        is SettingsEvent.OnShowFeaturedCurrenciesToggle -> state

        is SettingsEvent.OnAppVersionLoaded -> state.copy(appVersion = event.version)
        is SettingsEvent.OnLaunchPairModeChanged -> state.copy(launchPairMode = event.mode)
        is SettingsEvent.OnDefaultPairChanged -> state.copy(defaultPair = event.pair)
        is SettingsEvent.OnShowFeaturedPairsChanged -> state.copy(showFeaturedPairs = event.isEnabled)
        is SettingsEvent.OnShowFeaturedCurrenciesChanged -> state.copy(showFeaturedCurrencies = event.isEnabled)
        is SettingsEvent.OnThemeModeChanged -> state.copy(themeMode = event.mode)
        is SettingsEvent.OnDialogStateChanged -> state.copy(dialogState = event.dialogState)
        is SettingsEvent.OnLastRatesFreshnessChanged -> state.copy(lastRatesFreshness = event.freshness)
        is SettingsEvent.OnLastRatesTimestampChanged -> state.copy(lastRatesTimestamp = event.timestamp)
        is SettingsEvent.OnRefreshStateChanged -> state.copy(isRefreshing = event.isRefreshing)
    }
}

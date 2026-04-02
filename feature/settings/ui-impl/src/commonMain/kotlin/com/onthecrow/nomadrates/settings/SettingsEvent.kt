package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.uicore.Event
import com.onthecrow.nomadrates.util.theme.ThemeMode

internal sealed interface SettingsEvent : Event {
    data object OnBackPress : SettingsEvent
    data object OnRefreshClick : SettingsEvent
    data object OnLaunchPairModeClick : SettingsEvent
    data object OnDefaultPairFromClick : SettingsEvent
    data object OnDefaultPairToClick : SettingsEvent
    data object OnThemeClick : SettingsEvent
    data object OnPrivacyPolicyClick : SettingsEvent
    data object OnAboutDataSourceClick : SettingsEvent
    data class OnLaunchPairModeSelected(val mode: LaunchPairMode) : SettingsEvent
    data class OnDefaultPairChanged(val pair: SelectedConversionPair) : SettingsEvent
    data class OnThemeModeSelected(val mode: ThemeMode) : SettingsEvent
    data class OnShowFeaturedPairsToggle(val isEnabled: Boolean) : SettingsEvent
    data class OnShowFeaturedCurrenciesToggle(val isEnabled: Boolean) : SettingsEvent
    data class OnLaunchPairModeChanged(val mode: LaunchPairMode) : SettingsEvent
    data class OnShowFeaturedPairsChanged(val isEnabled: Boolean) : SettingsEvent
    data class OnShowFeaturedCurrenciesChanged(val isEnabled: Boolean) : SettingsEvent
    data class OnThemeModeChanged(val mode: ThemeMode) : SettingsEvent
    data class OnDialogStateChanged(val dialogState: SettingsDialogState?) : SettingsEvent
    data class OnLastRatesTimestampChanged(val timestamp: Long?) : SettingsEvent
    data class OnLastRatesFreshnessChanged(val freshness: SettingsRatesFreshness) : SettingsEvent
    data class OnAppVersionLoaded(val version: String) : SettingsEvent
    data class OnRefreshStateChanged(val isRefreshing: Boolean) : SettingsEvent
}

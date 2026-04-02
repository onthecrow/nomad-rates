package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.Event

internal sealed interface SettingsEvent : Event {
    data object OnBackPress : SettingsEvent
    data object OnRefreshClick : SettingsEvent
    data object OnPrivacyPolicyClick : SettingsEvent
    data class OnLastRatesTimestampChanged(val timestamp: Long?) : SettingsEvent
    data class OnLastRatesFreshnessChanged(val freshness: SettingsRatesFreshness) : SettingsEvent
    data class OnAppVersionLoaded(val version: String) : SettingsEvent
    data class OnRefreshStateChanged(val isRefreshing: Boolean) : SettingsEvent
}

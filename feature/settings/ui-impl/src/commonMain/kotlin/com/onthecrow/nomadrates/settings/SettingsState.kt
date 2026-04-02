package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.State

internal enum class SettingsRatesFreshness {
    Fresh,
    Stale,
    Unknown,
}

internal data class SettingsState(
    val lastRatesTimestamp: Long? = null,
    val lastRatesFreshness: SettingsRatesFreshness = SettingsRatesFreshness.Unknown,
    val isRefreshing: Boolean = false,
    val appVersion: String = "",
) : State

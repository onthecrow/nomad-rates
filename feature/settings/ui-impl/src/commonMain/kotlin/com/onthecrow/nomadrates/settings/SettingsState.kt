package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.uicore.State
import com.onthecrow.nomadrates.util.theme.ThemeMode

internal enum class SettingsRatesFreshness {
    Fresh,
    Stale,
    Unknown,
}

internal data class SettingsState(
    val lastRatesTimestamp: Long? = null,
    val lastRatesFreshness: SettingsRatesFreshness = SettingsRatesFreshness.Unknown,
    val launchPairMode: LaunchPairMode = LaunchPairMode.REMEMBER_LAST_PAIR,
    val defaultPair: SelectedConversionPair = SelectedConversionPair.DEFAULT,
    val showFeaturedPairs: Boolean = true,
    val showFeaturedCurrencies: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dialogState: SettingsDialogState? = null,
    val isRefreshing: Boolean = false,
    val appVersion: String = "",
    val privacyPolicyUrl: String = "",
    val dataSourceUrl: String = "",
) : State

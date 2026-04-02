package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsReducerTest {
    private val reducer = SettingsReducer()

    @Test
    fun onBackPress_doesNotChangeState() = kotlinx.coroutines.test.runTest {
        val initialState = SettingsState()

        val reducedState = reducer.reduce(
            state = initialState,
            event = SettingsEvent.OnBackPress,
        )

        assertEquals(initialState, reducedState)
    }

    @Test
    fun onAppVersionLoaded_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnAppVersionLoaded("1.0"),
        )

        assertEquals("1.0", reducedState.appVersion)
    }

    @Test
    fun onRefreshStateChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnRefreshStateChanged(true),
        )

        assertEquals(true, reducedState.isRefreshing)
    }

    @Test
    fun onLastRatesTimestampChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnLastRatesTimestampChanged(123L),
        )

        assertEquals(123L, reducedState.lastRatesTimestamp)
    }

    @Test
    fun onLastRatesFreshnessChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnLastRatesFreshnessChanged(SettingsRatesFreshness.Stale),
        )

        assertEquals(SettingsRatesFreshness.Stale, reducedState.lastRatesFreshness)
    }

    @Test
    fun onLaunchPairModeChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnLaunchPairModeChanged(LaunchPairMode.USE_DEFAULT_PAIR),
        )

        assertEquals(LaunchPairMode.USE_DEFAULT_PAIR, reducedState.launchPairMode)
    }

    @Test
    fun onDefaultPairChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val pair = SelectedConversionPair("GBP", "JPY")

        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnDefaultPairChanged(pair),
        )

        assertEquals(pair, reducedState.defaultPair)
    }

    @Test
    fun onShowFeaturedPairsChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnShowFeaturedPairsChanged(false),
        )

        assertEquals(false, reducedState.showFeaturedPairs)
    }

    @Test
    fun onShowFeaturedCurrenciesChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnShowFeaturedCurrenciesChanged(false),
        )

        assertEquals(false, reducedState.showFeaturedCurrencies)
    }

    @Test
    fun onThemeModeChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnThemeModeChanged(ThemeMode.DARK),
        )

        assertEquals(ThemeMode.DARK, reducedState.themeMode)
    }

    @Test
    fun onDialogStateChanged_updatesState() = kotlinx.coroutines.test.runTest {
        val reducedState = reducer.reduce(
            state = SettingsState(),
            event = SettingsEvent.OnDialogStateChanged(SettingsDialogState.ThemePicker),
        )

        assertEquals(SettingsDialogState.ThemePicker, reducedState.dialogState)
    }
}

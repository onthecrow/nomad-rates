package com.onthecrow.nomadrates.settings

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
}

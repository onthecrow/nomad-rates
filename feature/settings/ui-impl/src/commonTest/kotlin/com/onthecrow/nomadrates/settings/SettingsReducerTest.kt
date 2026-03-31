package com.onthecrow.nomadrates.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsReducerTest {
    private val reducer = SettingsReducer()

    @Test
    fun onBackPress_doesNotChangeState() = kotlinx.coroutines.test.runTest {
        val initialState = SettingsState

        val reducedState = reducer.reduce(
            state = initialState,
            event = SettingsEvent.OnBackPress,
        )

        assertEquals(initialState, reducedState)
    }
}

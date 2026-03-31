package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.Reducer

internal class SettingsReducer : Reducer<SettingsState, SettingsEvent> {
    override suspend fun reduce(
        state: SettingsState,
        event: SettingsEvent,
    ): SettingsState = state
}

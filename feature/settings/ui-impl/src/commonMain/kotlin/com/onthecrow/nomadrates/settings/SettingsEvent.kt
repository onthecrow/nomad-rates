package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.uicore.Event

internal sealed interface SettingsEvent : Event {
    data object OnBackPress : SettingsEvent
}

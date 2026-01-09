package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.uicore.Event

internal sealed interface ConversionEvent: Event {
    data object OnBackPress : ConversionEvent
    // todo tmp
    data object OnButtonPress : ConversionEvent
}

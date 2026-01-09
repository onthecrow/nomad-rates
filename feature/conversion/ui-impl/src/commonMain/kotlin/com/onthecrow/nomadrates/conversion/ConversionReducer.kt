package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.uicore.Reducer

internal class ConversionReducer : Reducer<ConversionState, ConversionEvent> {
    override fun reduce(
        state: ConversionState,
        event: ConversionEvent
    ): ConversionState {
        return when (event) {
            else -> state
        }
    }
}

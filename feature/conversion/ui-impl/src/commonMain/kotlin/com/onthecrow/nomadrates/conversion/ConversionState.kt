package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyState
import com.onthecrow.nomadrates.uicore.State

internal data class ConversionState(
    val from: ConversionCurrencyState? = null,
    val to: ConversionCurrencyState? = null,
) : State

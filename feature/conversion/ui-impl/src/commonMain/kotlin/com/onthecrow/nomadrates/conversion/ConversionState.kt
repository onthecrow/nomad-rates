package com.onthecrow.nomadrates.conversion

import androidx.compose.ui.graphics.Color
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyState
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.State

internal data class ConversionState(
    val from: ConversionCurrencyState? = null,
    val to: ConversionCurrencyState? = null,
    val historicalRates: List<Double>? = null,
    val historicalRatesColor: Color = SageGreen,
    val conversionListItems: List<ConversionListItem> = emptyList(),
) : State

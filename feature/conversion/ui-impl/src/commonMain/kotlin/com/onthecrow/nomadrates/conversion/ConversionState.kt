package com.onthecrow.nomadrates.conversion

import androidx.compose.ui.graphics.Color
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.State

internal data class ConversionState(
    val conversionViewState: ConversionViewState = ConversionViewState(),
    val chartColor: Color = SageGreen,
    val chartData: List<Double> = emptyList(),
    val conversionListItems: List<ConversionListItem> = emptyList(),
) : State

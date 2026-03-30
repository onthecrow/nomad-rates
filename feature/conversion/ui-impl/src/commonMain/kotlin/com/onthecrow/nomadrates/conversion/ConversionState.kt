package com.onthecrow.nomadrates.conversion

import androidx.compose.ui.graphics.Color
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.State
import org.jetbrains.compose.resources.StringResource

internal sealed interface ConversionState : State {
    data object Loading : ConversionState

    data class Error(
        val messageRes: StringResource,
    ) : ConversionState

    data class Content(
        val conversionViewState: ConversionViewState = ConversionViewState(),
        val chartColor: Color = SageGreen,
        val chartData: List<Double> = emptyList(),
        val conversionListItems: List<ConversionListItem> = emptyList(),
    ) : ConversionState
}

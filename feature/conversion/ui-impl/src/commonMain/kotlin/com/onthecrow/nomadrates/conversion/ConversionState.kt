package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.conversion.model.ChartTrend
import com.onthecrow.nomadrates.uicore.State

internal sealed interface ConversionState : State {
    data object Loading : ConversionState

    data class Error(
        val isRetrying: Boolean = false,
    ) : ConversionState

    data class Content(
        val conversionViewState: ConversionViewState.Content? = null,
        val chartTrend: ChartTrend = ChartTrend.POSITIVE,
        val chartData: List<Double> = emptyList(),
        val conversionListItems: List<ConversionListItem> = emptyList(),
    ) : ConversionState
}

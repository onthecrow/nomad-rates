package com.onthecrow.nomadrates.conversion.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.onthecrow.nomadrates.conversion.model.ChartTrend
import com.onthecrow.nomadrates.ui.negativeAccent
import com.onthecrow.nomadrates.ui.positiveAccent

@Composable
internal fun ChartTrend.toColor(): Color {
    return when (this) {
        ChartTrend.POSITIVE -> MaterialTheme.colorScheme.positiveAccent
        ChartTrend.NEGATIVE -> MaterialTheme.colorScheme.negativeAccent
    }
}

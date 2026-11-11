package com.onthecrow.nomadrates.conversion.model

internal enum class ChartTrend {
    POSITIVE,
    NEGATIVE,
}

internal fun List<Double>.toChartTrend(): ChartTrend {
    return if (size >= 2 && first() < last()) {
        ChartTrend.POSITIVE
    } else {
        ChartTrend.NEGATIVE
    }
}

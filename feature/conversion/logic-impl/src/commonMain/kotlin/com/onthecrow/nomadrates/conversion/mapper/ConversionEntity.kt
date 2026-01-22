package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.conversion.data.database.ConversionEntity

internal fun Pair<String, String>.toConversionEntity(): ConversionEntity {
    return ConversionEntity(
        fromCurrencyCode = first,
        toCurrencyCode = second,
        isFeatured = true,
        isFavorite = false,
    )
}

package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair

/**
 * The pairs the conversion screen lists. With featured pairs hidden, a featured pair the user added
 * to favourites stays in the list, as a favourite only.
 */
internal fun List<ConversionPair>.visiblePairs(showFeaturedPairs: Boolean): List<ConversionPair> {
    if (showFeaturedPairs) return this
    return mapNotNull { conversionPair ->
        when {
            !conversionPair.isFeatured -> conversionPair
            conversionPair.isFavourite -> conversionPair.copy(isFeatured = false)
            else -> null
        }
    }
}

package com.onthecrow.nomadrates.favouritesconversion.domain.model

/**
 * Which currency of the selected conversion pair the favourites conversion is based on: the one of
 * the field the user came from on the conversion screen.
 */
enum class ConversionPairSide {
    FROM,
    TO,
}

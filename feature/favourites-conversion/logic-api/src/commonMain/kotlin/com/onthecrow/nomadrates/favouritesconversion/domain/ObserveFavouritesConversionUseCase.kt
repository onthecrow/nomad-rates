package com.onthecrow.nomadrates.favouritesconversion.domain

import com.onthecrow.nomadrates.favouritesconversion.domain.model.ConversionPairSide
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import kotlinx.coroutines.flow.Flow

/**
 * The favourites converted from the [side] currency of the selected conversion pair.
 */
interface ObserveFavouritesConversionUseCase {
    operator fun invoke(side: ConversionPairSide): Flow<FavouritesConversion>
}

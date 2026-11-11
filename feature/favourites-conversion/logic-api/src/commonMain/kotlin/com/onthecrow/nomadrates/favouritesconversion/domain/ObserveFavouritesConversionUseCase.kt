package com.onthecrow.nomadrates.favouritesconversion.domain

import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import kotlinx.coroutines.flow.Flow

interface ObserveFavouritesConversionUseCase {
    operator fun invoke(): Flow<FavouritesConversion>
}

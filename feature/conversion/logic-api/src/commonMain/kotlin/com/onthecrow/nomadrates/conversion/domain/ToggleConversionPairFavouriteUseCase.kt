package com.onthecrow.nomadrates.conversion.domain

interface ToggleConversionPairFavouriteUseCase {
    suspend operator fun invoke(currencyFromCode: String, currencyToCode: String)
}

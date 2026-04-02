package com.onthecrow.nomadrates.settings.domain

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair

interface SetDefaultPairUseCase {
    suspend operator fun invoke(pair: SelectedConversionPair)
}

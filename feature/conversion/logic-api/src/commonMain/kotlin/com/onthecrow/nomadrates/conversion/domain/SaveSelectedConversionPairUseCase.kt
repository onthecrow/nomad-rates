package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair

interface SaveSelectedConversionPairUseCase {
    suspend operator fun invoke(selectedConversionPair: SelectedConversionPair)
}

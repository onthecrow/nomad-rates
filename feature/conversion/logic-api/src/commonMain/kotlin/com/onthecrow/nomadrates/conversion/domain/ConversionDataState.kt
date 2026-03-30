package com.onthecrow.nomadrates.conversion.domain

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair

sealed interface ConversionDataState {
    data object Loading : ConversionDataState
    data object Error : ConversionDataState
    data class Content(
        val activePair: ConversionPair,
        val conversionPairs: List<ConversionPair>,
    ) : ConversionDataState
}

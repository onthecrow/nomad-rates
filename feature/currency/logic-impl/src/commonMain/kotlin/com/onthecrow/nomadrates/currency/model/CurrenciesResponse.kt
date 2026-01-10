package com.onthecrow.nomadrates.currency.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CurrenciesResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)

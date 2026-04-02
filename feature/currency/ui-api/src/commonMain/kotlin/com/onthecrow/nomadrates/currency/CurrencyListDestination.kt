package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyListDestination(
    val source: CurrencySelectionSource,
) : Destination

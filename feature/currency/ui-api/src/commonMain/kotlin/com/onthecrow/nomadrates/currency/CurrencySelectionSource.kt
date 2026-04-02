package com.onthecrow.nomadrates.currency

import kotlinx.serialization.Serializable

@Serializable
enum class CurrencySelectionSource {
    ConversionFrom,
    ConversionTo,
    SettingsDefaultFrom,
    SettingsDefaultTo,
}

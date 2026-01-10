package com.onthecrow.nomadrates.currency.mapper

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toCurrencyName
import com.onthecrow.nomadrates.util.toIsoCountryCode

internal fun Currency.toUi(): CurrencyUI {
    return CurrencyUI(
        flagIcon = this.code.toIsoCountryCode().toFlagResourceUri(),
        nameShort = this.code.uppercase(),
        nameLong = this.code.toCurrencyName(),
    )
}

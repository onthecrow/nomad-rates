package com.onthecrow.nomadrates.currency.mapper

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyUI
import com.onthecrow.nomadrates.util.toCurrencyName
import com.onthecrow.nomadrates.util.toIsoCountryCode
import nomadrates.feature.currency.impl.generated.resources.Res

fun Currency.toUi(): CurrencyUI {
    return CurrencyUI(
        flagIcon = this.code.toIsoCountryCode().toFlagResourceUri(),
        nameShort = this.code.uppercase(),
        nameLong = this.code.toCurrencyName(),
    )
}

private fun String?.toFlagResourceUri(): String {
    return this?.let {
        try {
            Res.getUri("files/flags/${this.lowercase()}.svg")
        } catch (error: Throwable) {
            println("error: $error")
            null
        }
        // TODO need to implement a placeholder or smth
    } ?: Res.getUri("files/flags/ae.svg")
}

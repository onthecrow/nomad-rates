package com.onthecrow.nomadrates.util

import java.util.Currency
import java.util.Locale

actual fun String.toCurrencyName(): String {
    return runCatching {
        Currency.getInstance(this).getDisplayName(Locale.getDefault())
    }.getOrDefault(this)
}

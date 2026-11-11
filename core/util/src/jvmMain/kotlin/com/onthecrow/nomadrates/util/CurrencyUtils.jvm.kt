package com.onthecrow.nomadrates.util

import java.util.Currency
import java.util.Locale

internal actual fun String.toPlatformCurrencyName(useEnglishName: Boolean): String {
    val locale = if (useEnglishName) Locale.ENGLISH else Locale.getDefault()
    return runCatching {
        Currency.getInstance(this).getDisplayName(locale)
    }.getOrDefault(this)
}

package com.onthecrow.nomadrates.util

import android.util.Log
import java.util.Currency
import java.util.Locale

internal actual fun String.toPlatformCurrencyName(useEnglishName: Boolean): String {
    val locale = if (useEnglishName) Locale.ENGLISH else Locale.getDefault()
    return try {
        Currency.getInstance(this).getDisplayName(locale)
    } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, e.message, e)
        this
    }
}

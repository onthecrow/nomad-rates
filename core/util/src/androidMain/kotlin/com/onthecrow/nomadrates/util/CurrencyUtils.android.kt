package com.onthecrow.nomadrates.util

import android.util.Log
import java.util.Currency
import java.util.Locale

actual fun String.toCurrencyName(): String {
    return try {
        Currency.getInstance(this).getDisplayName(Locale.getDefault())
    } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, e.message, e)
        this
    }
}

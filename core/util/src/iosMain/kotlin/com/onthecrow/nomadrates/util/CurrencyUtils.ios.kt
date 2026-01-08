package com.onthecrow.nomadrates.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun String.toCurrencyName(): String {
    val locale = NSLocale.currentLocale
    return locale.displayNameForKey(platform.Foundation.NSLocaleCurrencyCode, this) ?: this
}

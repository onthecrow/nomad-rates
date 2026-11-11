package com.onthecrow.nomadrates.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.preferredLanguages

internal actual fun String.toPlatformCurrencyName(useEnglishName: Boolean): String {
    val locale = if (useEnglishName) NSLocale(localeIdentifier = "en") else deviceLocale()
    return locale.displayNameForKey(platform.Foundation.NSLocaleCurrencyCode, this) ?: this
}

// currentLocale is limited to the app's localizations, so it would stay English on any device
private fun deviceLocale(): NSLocale {
    val language = NSLocale.preferredLanguages.firstOrNull() as? String
    return language?.let { NSLocale(localeIdentifier = it) } ?: NSLocale.currentLocale
}

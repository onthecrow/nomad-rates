package com.onthecrow.nomadrates.favouritesconversion.mapper

import com.onthecrow.nomadrates.favouritesconversion.model.CompactAmount
import com.onthecrow.nomadrates.favouritesconversion.model.FavouritesConversionListItem
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversionTarget
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toCurrencyName
import com.onthecrow.nomadrates.util.toFlagAssetKey
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

/** Amounts from this one up may be shown in [CompactAmount] form when they do not fit. */
private const val COMPACT_AMOUNT_THRESHOLD = 1_000_000.0
private const val COMPACT_MANTISSA_SCALE = 100.0

internal fun FavouritesConversionTarget.toFavouritesConversionListItem(
    amount: String,
    useEnglishCurrencyNames: Boolean,
): FavouritesConversionListItem {
    val convertedAmount = convertAmount(
        amount = amount,
        conversionRate = conversionRate,
    )
    return FavouritesConversionListItem(
        flagIcon = currency.code.toFlagAssetKey().toFlagResourceUri(),
        currencyCode = currency.code.uppercase(),
        currencyName = currency.code.toCurrencyName(useEnglishCurrencyNames),
        conversionRate = conversionRate,
        convertedAmount = convertedAmount.formatAdaptive(currency.code),
        compactAmount = convertedAmount.toCompactAmount(currency.code),
    )
}

internal fun FavouritesConversionListItem.withAmount(amount: String): FavouritesConversionListItem {
    val convertedAmount = convertAmount(
        amount = amount,
        conversionRate = conversionRate,
    )
    return copy(
        convertedAmount = convertedAmount.formatAdaptive(currencyCode),
        compactAmount = convertedAmount.toCompactAmount(currencyCode),
    )
}

/**
 * [amount] is already filtered input: digits and at most one `.`, possibly empty or unfinished ("12.").
 */
internal fun convertAmount(
    amount: String,
    conversionRate: Double,
): MoneyAmount {
    val normalizedAmount = amount
        .trimEnd('.')
        .let { value -> if (value.startsWith('.')) "0$value" else value }
    return if (normalizedAmount.isEmpty()) {
        MoneyAmount(0)
    } else {
        MoneyAmount(normalizedAmount) * MoneyAmount(conversionRate)
    }
}

/**
 * Scientific notation with two decimals in the mantissa, for amounts of [COMPACT_AMOUNT_THRESHOLD]
 * and more; null for smaller amounts.
 */
internal fun MoneyAmount.toCompactAmount(currencyCode: String): CompactAmount? {
    val value = toDouble()
    if (!value.isFinite() || abs(value) < COMPACT_AMOUNT_THRESHOLD) return null

    var exponent = floor(log10(abs(value))).toInt()
    var mantissa = round(value / 10.0.pow(exponent) * COMPACT_MANTISSA_SCALE) / COMPACT_MANTISSA_SCALE
    if (abs(mantissa) >= 10.0) {
        // 9.996 × 10^n rounds up to 10.00 × 10^n, which is 1.00 × 10^(n+1).
        mantissa /= 10.0
        exponent += 1
    }
    return CompactAmount(
        mantissa = MoneyAmount(mantissa).formatAdaptive(currencyCode),
        exponent = exponent,
    )
}

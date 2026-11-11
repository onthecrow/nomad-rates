package com.onthecrow.nomadrates.favouritesconversion.mapper

import com.onthecrow.nomadrates.favouritesconversion.model.FavouritesConversionListItem
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversionTarget
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toCurrencyName
import com.onthecrow.nomadrates.util.toFlagAssetKey

internal fun FavouritesConversionTarget.toFavouritesConversionListItem(
    amount: String,
    useEnglishCurrencyNames: Boolean,
): FavouritesConversionListItem {
    return FavouritesConversionListItem(
        flagIcon = currency.code.toFlagAssetKey().toFlagResourceUri(),
        currencyCode = currency.code.uppercase(),
        currencyName = currency.code.toCurrencyName(useEnglishCurrencyNames),
        conversionRate = conversionRate,
        convertedAmount = convertAmount(
            amount = amount,
            conversionRate = conversionRate,
            currencyCode = currency.code,
        ),
    )
}

internal fun FavouritesConversionListItem.withAmount(amount: String): FavouritesConversionListItem {
    return copy(
        convertedAmount = convertAmount(
            amount = amount,
            conversionRate = conversionRate,
            currencyCode = currencyCode,
        ),
    )
}

/**
 * [amount] is already filtered input: digits and at most one `.`, possibly empty or unfinished ("12.").
 */
internal fun convertAmount(
    amount: String,
    conversionRate: Double,
    currencyCode: String,
): String {
    val normalizedAmount = amount
        .trimEnd('.')
        .let { value -> if (value.startsWith('.')) "0$value" else value }
    val convertedAmount = if (normalizedAmount.isEmpty()) {
        MoneyAmount(0)
    } else {
        MoneyAmount(normalizedAmount) * MoneyAmount(conversionRate)
    }
    return convertedAmount.formatAdaptive(currencyCode)
}

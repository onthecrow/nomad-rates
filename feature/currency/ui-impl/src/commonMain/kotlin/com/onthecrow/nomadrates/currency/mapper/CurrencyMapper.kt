package com.onthecrow.nomadrates.currency.mapper

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toFlagAssetKey
import com.onthecrow.nomadrates.util.toCurrencyName
import nomadrates.feature.currency.ui_impl.generated.resources.Res
import nomadrates.feature.currency.ui_impl.generated.resources.currency_list_all_header
import nomadrates.feature.currency.ui_impl.generated.resources.currency_list_favourites_header
import nomadrates.feature.currency.ui_impl.generated.resources.currency_list_featured_header
import org.jetbrains.compose.resources.getString

internal suspend fun List<Currency>.toUi(
    showFeaturedCurrencies: Boolean = true,
    useEnglishCurrencyNames: Boolean = true,
): List<CurrencyListItem> {
    val featuredCurrencies = if (showFeaturedCurrencies) {
        this.filter { it.isFeatured }
    } else {
        emptyList()
    }
    val favouriteCurrencies = this.filter { it.isFavourite }
    return buildList {
        if (favouriteCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header(getString(Res.string.currency_list_favourites_header)))
            addAll(favouriteCurrencies.map { it.toUi(useEnglishCurrencyNames, ListGroup.FAVOURITE) }
                .sortedBy { it.currencyCode })
        }
        if (featuredCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header(getString(Res.string.currency_list_featured_header)))
            addAll(featuredCurrencies.map { it.toUi(useEnglishCurrencyNames, ListGroup.FEATURED) }
                .sortedBy { it.currencyCode })
        }
        if (favouriteCurrencies.isNotEmpty() || featuredCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header(getString(Res.string.currency_list_all_header)))
        }
        addAll(this@toUi.map { it.toUi(useEnglishCurrencyNames, ListGroup.ALL) }.sortedBy { it.currencyCode })
    }
}

private fun Currency.toUi(
    useEnglishCurrencyNames: Boolean,
    listGroup: ListGroup,
): CurrencyListItem.Data {
    return CurrencyListItem.Data(
        flagIcon = code.toFlagAssetKey().toFlagResourceUri(),
        currencyCode = code.uppercase(),
        currencyName = code.toCurrencyName(useEnglishCurrencyNames),
        alternateCurrencyName = code.toCurrencyName(!useEnglishCurrencyNames),
        isFavourite = isFavourite,
        isFeatured = isFeatured,
        listGroup = listGroup,
    )
}

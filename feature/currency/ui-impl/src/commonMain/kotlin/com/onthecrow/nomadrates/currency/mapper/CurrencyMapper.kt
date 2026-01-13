package com.onthecrow.nomadrates.currency.mapper

import com.onthecrow.nomadrates.currency.model.Currency
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toCurrencyName
import com.onthecrow.nomadrates.util.toIsoCountryCode

internal fun List<Currency>.toUi(): List<CurrencyListItem> {
    val featuredCurrencies = this.filter { it.isFeatured }
    val favouriteCurrencies = this.filter { it.isFavourite }
    return buildList {
        if (featuredCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header("Featured"))
            addAll(featuredCurrencies.map{ it.toUi(listGroup = ListGroup.FEATURED) })
        }
        if (favouriteCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header("Favourites"))
            addAll(favouriteCurrencies.map{ it.toUi(listGroup = ListGroup.FAVOURITE) })
        }
        if (favouriteCurrencies.isNotEmpty() || featuredCurrencies.isNotEmpty()) {
            add(CurrencyListItem.Header("All currencies"))
        }
        addAll(this@toUi.map(Currency::toUi))
    }
}

private fun Currency.toUi(
    listGroup: ListGroup = ListGroup.ALL,
): CurrencyListItem {
    return CurrencyListItem.Data(
        flagIcon = code.toIsoCountryCode().toFlagResourceUri(),
        currencyCode = code.uppercase(),
        currencyName = code.toCurrencyName(),
        isFavourite = isFavourite,
        isFeatured = isFeatured,
        listGroup = listGroup,
    )
}

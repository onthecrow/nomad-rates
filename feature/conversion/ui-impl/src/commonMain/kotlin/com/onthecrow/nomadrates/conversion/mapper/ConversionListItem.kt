package com.onthecrow.nomadrates.conversion.mapper

import com.onthecrow.nomadrates.conversion.domain.model.ConversionPair
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.MutedClay
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.util.toIsoCountryCode

internal fun List<ConversionPair>.toConversionListItems(): List<ConversionListItem> {
    val featuredPairs = this.filter { conversionPair -> conversionPair.isFeatured }
        .map { it.toConversionPair(ListGroup.FEATURED) }
        .sortedByDescending { it.title }
    val favouritePairs = this.filter { conversionPair -> conversionPair.isFavourite }
        .map { it.toConversionPair(ListGroup.FAVOURITE) }
        .sortedByDescending { it.title }

    return buildList {
        if (favouritePairs.isNotEmpty()) {
            addAll(favouritePairs)
            add(ConversionListItem.Header("Favourites"))
        }
        if (featuredPairs.isNotEmpty()) {
            addAll(featuredPairs)
            add(ConversionListItem.Header("Featured"))
        }
    }
}

private fun ConversionPair.toConversionPair(group: ListGroup): ConversionListItem.Data {
    // TODO use this entity across whole app
    val rate = MoneyAmount(this.conversionRate)
    return ConversionListItem.Data(
        fromIcon = fromCurrency.code.toIsoCountryCode().toFlagResourceUri(),
        toIcon = toCurrency.code.toIsoCountryCode().toFlagResourceUri(),
        title = "${fromCurrency.code} — ${toCurrency.code}",
        subtitle = "1 ${fromCurrency.code} = ${rate.formatAdaptive(toCurrency.code)} ${toCurrency.code}",
        chartData = historicalRates,
        chartColor = if (historicalRates.size >= 2 && historicalRates.first() > historicalRates.last()) MutedClay else SageGreen,
        currencyPair = fromCurrency.code to toCurrency.code,
        isFavourite = isFavourite,
        listGroup = group,
    )
}

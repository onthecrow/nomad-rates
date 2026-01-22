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
        .map(ConversionPair::toConversionPair)
    val favouritePairs = this.filter { conversionPair -> conversionPair.isFavourite }
        .map(ConversionPair::toConversionPair)
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

private fun ConversionPair.toConversionPair(): ConversionListItem.Data {
    // TODO use this entity across whole app
    val rate = MoneyAmount(this.conversionRate)
    return ConversionListItem.Data(
        fromIcon = fromCurrency.code.toIsoCountryCode().toFlagResourceUri(),
        toIcon = toCurrency.code.toIsoCountryCode().toFlagResourceUri(),
        title = "${fromCurrency.code} to ${toCurrency.code}",
        subtitle = "1 ${fromCurrency.code} = ${rate.formatAdaptive(toCurrency.code)} ${toCurrency.code}",
        chartData = historicalRates,
        chartColor = if (historicalRates.size >= 2 && historicalRates.first() > historicalRates.last()) MutedClay else SageGreen,
        listGroup = if (isFeatured) ListGroup.FEATURED else ListGroup.FAVOURITE,
    )
}

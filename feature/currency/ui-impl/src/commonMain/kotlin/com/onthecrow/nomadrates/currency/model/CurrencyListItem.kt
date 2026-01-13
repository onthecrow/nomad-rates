package com.onthecrow.nomadrates.currency.model

internal sealed interface CurrencyListItem {

    val listKey: String

    data class Header(
        val title: String,
    ) : CurrencyListItem {
        override val listKey: String = title
    }

    data class Data(
        val flagIcon: String,
        val currencyCode: String,
        val currencyName: String,
        val isFavourite: Boolean,
        val isFeatured: Boolean,
        val listGroup: ListGroup,
    ) : CurrencyListItem {
        override val listKey: String = "key_${currencyCode}_${listGroup.name}"
    }
}

internal enum class ListGroup {
    FEATURED,
    FAVOURITE,
    ALL,
}

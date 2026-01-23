package com.onthecrow.nomadrates.conversion.model

import androidx.compose.ui.graphics.Color

internal sealed interface ConversionListItem {

    val listKey: String

    data class Header(
        val title: String,
    ) : ConversionListItem {
        override val listKey: String = title
    }

    data class Data(
        val fromIcon: String,
        val toIcon: String,
        val title: String,
        val subtitle: String,
        val chartData: List<Double>,
        val chartColor: Color,
        val currencyPair: Pair<String, String>,
        val isFavourite: Boolean,
        val listGroup: ListGroup,
    ) : ConversionListItem {
        override val listKey: String = "key_${title}_${listGroup.name}"
    }
}

internal enum class ListGroup {
    FEATURED,
    FAVOURITE,
}

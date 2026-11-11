package com.onthecrow.nomadrates.currency.view

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup

internal class CurrencyListItemPreviewParameterProvider :
    PreviewParameterProvider<CurrencyListItem> {
    override val values = sequenceOf(
        CurrencyListItem.Header("Featured"),
        CurrencyListItem.Data(
            flagIcon = "",
            currencyCode = "USD",
            currencyName = "US Dollar",
            alternateCurrencyName = "Доллар США",
            isFavourite = true,
            isFeatured = true,
            listGroup = ListGroup.ALL,
        ),
        CurrencyListItem.Data(
            flagIcon = "",
            currencyCode = "USD",
            currencyName = "US Dollar",
            alternateCurrencyName = "Доллар США",
            isFavourite = false,
            isFeatured = true,
            listGroup = ListGroup.ALL,
        ),
    )
}

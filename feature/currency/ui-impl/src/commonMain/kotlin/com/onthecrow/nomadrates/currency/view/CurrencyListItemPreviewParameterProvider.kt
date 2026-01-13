package com.onthecrow.nomadrates.currency.view

import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.currency.model.ListGroup
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

internal class CurrencyListItemPreviewParameterProvider : PreviewParameterProvider<CurrencyListItem> {
    override val values = sequenceOf(
        CurrencyListItem.Header("Featured"),
        CurrencyListItem.Data(
            flagIcon = "",
            currencyCode = "USD",
            currencyName = "US Dollar",
            isFavourite = true,
            isFeatured = true,
            listGroup = ListGroup.ALL,
        ),
    )
}

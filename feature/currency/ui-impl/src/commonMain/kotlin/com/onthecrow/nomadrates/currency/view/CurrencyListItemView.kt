package com.onthecrow.nomadrates.currency.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.CurrencyRowView
import com.onthecrow.nomadrates.ui.view.LikeButtonView

@Composable
internal fun CurrencyListItemView(
    state: CurrencyListItem,
    modifier: Modifier = Modifier,
    onItemClick: (CurrencyListItem.Data) -> Unit = {},
    onFavouriteClick: (CurrencyListItem.Data) -> Unit = {},
) {
    when (state) {
        is CurrencyListItem.Data -> DataItemView(
            modifier = modifier,
            state = state,
            onClick = onItemClick,
            onFavouriteClick = onFavouriteClick,
        )

        is CurrencyListItem.Header -> HeaderItemView(modifier = modifier, state = state)
    }
}

@Composable
private fun DataItemView(
    state: CurrencyListItem.Data,
    modifier: Modifier = Modifier,
    onClick: (CurrencyListItem.Data) -> Unit = {},
    onFavouriteClick: (CurrencyListItem.Data) -> Unit,
) {
    CurrencyRowView(
        modifier = modifier,
        flagIcon = state.flagIcon,
        currencyCode = state.currencyCode,
        currencyName = state.currencyName,
        onClick = { onClick(state) },
    ) {
        LikeButtonView(
            isFavourite = state.isFavourite,
            onClick = { onFavouriteClick(state) },
        )
    }
}

@Composable
private fun HeaderItemView(
    state: CurrencyListItem.Header,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = state.title,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
private fun CurrencyListItemViewPreview(
    @PreviewParameter(CurrencyListItemPreviewParameterProvider::class)
    state: CurrencyListItem,
) {
    NomadRatesTheme(darkTheme = true) {
        CurrencyListItemView(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        )
    }
}

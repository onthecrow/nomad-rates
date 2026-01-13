package com.onthecrow.nomadrates.currency.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.onthecrow.nomadrates.currency.model.CurrencyListItem
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
internal fun CurrencyListItemView(
    state: CurrencyListItem,
    modifier: Modifier = Modifier,
    onClick: (CurrencyListItem.Data) -> Unit = {},
) {
    when (state) {
        is CurrencyListItem.Data -> DataItemView(modifier = modifier, state = state, onClick = onClick)
        is CurrencyListItem.Header -> HeaderItemView(modifier = modifier, state = state)
    }
}

@Composable
private fun DataItemView(
    state: CurrencyListItem.Data,
    modifier: Modifier = Modifier,
    onClick: (CurrencyListItem.Data) -> Unit = {},
) {
    Row(
        modifier = modifier.clickable(
            enabled = true,
            onClick = { onClick(state) },
        )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = state.flagIcon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = state.currencyCode,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = state.currencyName,
            color = MaterialTheme.colorScheme.onBackground,
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

package com.onthecrow.nomadrates.conversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.view.ConversionView
import com.onthecrow.nomadrates.conversion.view.pair.ConversionListItemView
import com.onthecrow.nomadrates.ui.view.CurrencyChart

@Composable
internal fun ConversionScreen(
    state: ConversionState,
    modifier: Modifier = Modifier,
    onEvent: (ConversionEvent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .systemBarsPadding()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
        ) {
            items(
                items = state.conversionListItems,
                key = { it.listKey },
                contentType = { it::class }
            ) { listItem ->
                ConversionListItemView(
                    modifier = Modifier.fillMaxWidth(),
                    state = listItem,
                    onClick = { onEvent(ConversionEvent.OnConversionViewClick(it)) }
                )
            }
        }
        if (state.from != null && state.to != null) {
            ConversionView(
                modifier = Modifier,
                from = state.from,
                to = state.to,
                onEvent = onEvent,
            )
        }
        if (state.historicalRates != null) {
            CurrencyChart(
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp),
                data = state.historicalRates,
                graphColor = state.historicalRatesColor,
            )
        }
    }
}

@Preview
@Composable
private fun ConversionScreenPreview() {
    MaterialTheme {
        ConversionScreen(state = ConversionState())
    }
}

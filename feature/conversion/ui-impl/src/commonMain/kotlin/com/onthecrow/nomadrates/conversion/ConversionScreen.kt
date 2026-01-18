package com.onthecrow.nomadrates.conversion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.view.ConversionView
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (state.from != null && state.to != null) {
            ConversionView(
                modifier = Modifier.padding(horizontal = 16.dp),
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

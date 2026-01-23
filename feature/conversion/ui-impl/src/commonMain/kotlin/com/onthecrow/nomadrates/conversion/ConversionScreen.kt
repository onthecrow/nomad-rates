package com.onthecrow.nomadrates.conversion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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
            .navigationBarsPadding()
            .imePadding(),
//        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        val layoutDirections = LocalLayoutDirection.current
        val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
        var contentPadding by remember { mutableStateOf(PaddingValues()) }
        LaunchedEffect(systemBarsPadding, layoutDirections) {
            snapshotFlow { systemBarsPadding to layoutDirections }
                .collect { (systemBarsPadding, layoutDirections) ->
                    contentPadding = PaddingValues(
                        top = systemBarsPadding.calculateTopPadding(),
                        bottom = 16.dp,
                        start = systemBarsPadding.calculateStartPadding(layoutDirections),
                        end = systemBarsPadding.calculateEndPadding(layoutDirections),
                    )
                }
        }
        Box(
            modifier = Modifier.weight(1f)
                .fillMaxWidth(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true,
                contentPadding = contentPadding,
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
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ),
            )
        }
        if (state.activeConversionPair != null && state.from != null && state.to != null) {
            ConversionView(
                modifier = Modifier,
                from = state.from,
                to = state.to,
                onEvent = onEvent,
            )
            Spacer(modifier = Modifier.size(16.dp))
            CurrencyChart(
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp),
                data = state.activeConversionPair.historicalRates,
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

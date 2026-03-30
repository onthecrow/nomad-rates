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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.conversion.view.ConversionView
import com.onthecrow.nomadrates.conversion.view.pair.ConversionListItemView
import com.onthecrow.nomadrates.ui.view.CurrencyChart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConversionScreen(
    state: ConversionState,
    modifier: Modifier = Modifier,
    eventFlow: Flow<ConversionEvent> = emptyFlow(),
    onEvent: (ConversionEvent) -> Unit = {},
) {
    when (state) {
        ConversionState.Loading -> LoadingState(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
        )

        is ConversionState.Error -> ErrorState(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
            message = stringResource(state.messageRes),
            onRetryClick = { onEvent(ConversionEvent.OnRetryClick) },
        )

        is ConversionState.Content -> ContentState(
            state = state,
            modifier = modifier,
            eventFlow = eventFlow,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(onClick = onRetryClick) {
            Text(stringResource(Res.string.conversion_retry))
        }
    }
}

@Composable
private fun ContentState(
    state: ConversionState.Content,
    modifier: Modifier = Modifier,
    eventFlow: Flow<ConversionEvent> = emptyFlow(),
    onEvent: (ConversionEvent) -> Unit = {},
) {
    val layoutDirections = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    var contentPadding by remember { mutableStateOf(PaddingValues()) }

    LaunchedEffect(systemBarsPadding, layoutDirections) {
        snapshotFlow { systemBarsPadding to layoutDirections }
            .collect { (padding, directions) ->
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = 16.dp,
                    start = padding.calculateStartPadding(directions),
                    end = padding.calculateEndPadding(directions),
                )
            }
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(state, eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is ConversionEvent.OnItemAddToFavourite -> {
                    val newFavouriteIndex = state.conversionListItems.indexOfFirst { listItem ->
                        (listItem as? ConversionListItem.Data)?.currencyPair == event.conversionPair &&
                            listItem.listGroup == ListGroup.FAVOURITE
                    }
                    if (newFavouriteIndex >= 0) {
                        lazyListState.animateScrollToItem(newFavouriteIndex)
                    }
                }

                else -> {}
            }
        }
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true,
                state = lazyListState,
                contentPadding = contentPadding,
            ) {
                items(
                    items = state.conversionListItems,
                    key = { it.listKey },
                    contentType = { it::class },
                ) { listItem ->
                    ConversionListItemView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        state = listItem,
                        onClick = { onEvent(ConversionEvent.OnConversionViewClick(it.currencyPair)) },
                        onAddToFavouritesClick = {
                            onEvent(ConversionEvent.OnConversionPairFavouritesClick(it.currencyPair))
                        },
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
                                MaterialTheme.colorScheme.background,
                            )
                        )
                    ),
            )
        }
        if (state.conversionViewState.to != null && state.conversionViewState.from != null) {
            ConversionView(
                modifier = Modifier,
                state = state.conversionViewState,
                onEvent = onEvent,
            )
            Spacer(modifier = Modifier.size(16.dp))
            CurrencyChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                data = state.chartData,
                graphColor = state.chartColor,
            )
        }
    }
}

@Preview
@Composable
private fun ConversionScreenPreview() {
    MaterialTheme {
        ConversionScreen(state = ConversionState.Content())
    }
}

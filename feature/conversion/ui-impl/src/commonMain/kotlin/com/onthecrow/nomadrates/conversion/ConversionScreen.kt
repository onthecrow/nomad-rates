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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.conversion.view.ConversionView
import com.onthecrow.nomadrates.conversion.view.pair.ConversionListItemView
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.ui.view.CurrencyChart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_retry
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private val FeaturedSkeletonTitleShape = RoundedCornerShape(14.dp)
private val FeaturedSkeletonSubtitleShape = RoundedCornerShape(10.dp)
private val FeaturedSkeletonTitleWidth = 132.dp
private val FeaturedSkeletonSubtitleWidths = listOf(
    118.dp,
    126.dp,
    121.dp,
    130.dp,
)

@Composable
internal fun ConversionScreen(
    state: ConversionState,
    showFeaturedPairs: Boolean,
    modifier: Modifier = Modifier,
    eventFlow: Flow<ConversionEvent> = emptyFlow(),
    onEvent: (ConversionEvent) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
        ) {
        when (state) {
            is ConversionState.Loading -> LoadingState(
                showFeaturedPairs = showFeaturedPairs,
                modifier = Modifier.fillMaxSize(),
            )

            is ConversionState.Error -> ErrorState(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(state.messageRes),
                onRetryClick = { onEvent(ConversionEvent.OnRetryClick) },
            )

            is ConversionState.Content -> ContentState(
                state = state,
                modifier = Modifier.fillMaxSize(),
                eventFlow = eventFlow,
                onEvent = onEvent,
            )
        }

        SettingsFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            onClick = { onEvent(ConversionEvent.OnSettingsClick) },
        )
    }
}

@Composable
private fun SettingsFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = vectorResource(Res.drawable.ic_settings),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun LoadingState(
    showFeaturedPairs: Boolean,
    modifier: Modifier = Modifier,
) {
    val contentPadding = rememberContentPadding()

    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (showFeaturedPairs) {
                FeaturedLoadingSection(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(contentPadding),
                )
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
        ConversionView(
            state = ConversionViewState.Loading,
        )
        Spacer(modifier = Modifier.height(116.dp))
    }
}

@Composable
private fun FeaturedLoadingSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Featured",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
        )
        Column {
            repeat(4) { index ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier.size(72.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = CircleShape,
                                )
                                .padding(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmer(CircleShape),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 24.dp, top = 24.dp)
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = CircleShape,
                                )
                                .padding(4.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shimmer(CircleShape),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(FeaturedSkeletonTitleWidth)
                                .height(28.dp)
                                .shimmer(FeaturedSkeletonTitleShape),
                        )
                        Box(
                            modifier = Modifier
                                .width(FeaturedSkeletonSubtitleWidths[index])
                                .height(20.dp)
                                .shimmer(FeaturedSkeletonSubtitleShape),
                        )
                    }
                }
            }
        }
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
    val contentPadding = rememberContentPadding()
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

                else -> Unit
            }
        }
    }

    Column(
        modifier = modifier,
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
        state.conversionViewState?.let { conversionViewState ->
            ConversionView(
                state = conversionViewState,
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

@Composable
private fun rememberContentPadding(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    return PaddingValues(
        top = systemBarsPadding.calculateTopPadding(),
        bottom = 16.dp,
        start = systemBarsPadding.calculateStartPadding(layoutDirection),
        end = systemBarsPadding.calculateEndPadding(layoutDirection),
    )
}

@Preview
@Composable
private fun ConversionScreenPreview() {
    MaterialTheme {
        ConversionScreen(
            state = ConversionState.Content(),
            showFeaturedPairs = true,
        )
    }
}

@Preview
@Composable
private fun ConversionLoadingScreenPreview() {
    MaterialTheme {
        ConversionScreen(
            state = ConversionState.Loading,
            showFeaturedPairs = true,
        )
    }
}

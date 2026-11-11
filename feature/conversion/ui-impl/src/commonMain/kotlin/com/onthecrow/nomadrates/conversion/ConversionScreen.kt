package com.onthecrow.nomadrates.conversion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.conversion.view.ConversionView
import com.onthecrow.nomadrates.conversion.view.pair.ConversionListItemView
import com.onthecrow.nomadrates.conversion.view.toColor
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.ui.view.CurrencyChart
import com.onthecrow.nomadrates.ui.view.RefreshButtonView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_batch_button_description
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_error_body
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_error_title
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_retry
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_cloud_off
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_list
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private const val SWAP_BUTTON_HIDE_DURATION_MILLIS = 150

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
    val scope = rememberCoroutineScope()
    // The amount field flies over the swap button on the way to the batch conversion screen, so the
    // button shrinks away first and the transition starts after that.
    val swapButtonScale = remember { Animatable(1f) }
    LifecycleResumeEffect(swapButtonScale) {
        scope.launch { swapButtonScale.snapTo(1f) }
        onPauseOrDispose { }
    }

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
                isRetrying = state.isRetrying,
                onRetryClick = { onEvent(ConversionEvent.OnRetryClick) },
            )

            is ConversionState.Content -> ContentState(
                state = state,
                modifier = Modifier.fillMaxSize(),
                swapButtonScale = { swapButtonScale.value },
                eventFlow = eventFlow,
                onEvent = onEvent,
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BatchConversionFab(
                onClick = {
                    scope.launch {
                        swapButtonScale.animateTo(0f, tween(SWAP_BUTTON_HIDE_DURATION_MILLIS))
                        onEvent(ConversionEvent.OnBatchConversionClick)
                    }
                },
            )
            SettingsFab(
                onClick = { onEvent(ConversionEvent.OnSettingsClick) },
            )
        }
    }
}

@Composable
private fun BatchConversionFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    SmallFloatingActionButton(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = vectorResource(Res.drawable.ic_list),
            contentDescription = stringResource(Res.string.conversion_batch_button_description),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
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
                Row(
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
    isRetrying: Boolean,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = vectorResource(Res.drawable.ic_cloud_off),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.conversion_error_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.conversion_error_body),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            RefreshButtonView(
                text = stringResource(Res.string.conversion_retry),
                isLoading = isRetrying,
                onClick = onRetryClick,
            )
        }
    }
}

@Composable
private fun ContentState(
    state: ConversionState.Content,
    modifier: Modifier = Modifier,
    swapButtonScale: () -> Float = { 1f },
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
                swapButtonScale = swapButtonScale,
                onEvent = onEvent,
            )
            Spacer(modifier = Modifier.size(16.dp))
            CurrencyChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                data = state.chartData,
                graphColor = state.chartTrend.toColor(),
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

@Preview
@Composable
private fun ConversionErrorScreenPreview() {
    MaterialTheme {
        ConversionScreen(
            state = ConversionState.Error(),
            showFeaturedPairs = true,
        )
    }
}

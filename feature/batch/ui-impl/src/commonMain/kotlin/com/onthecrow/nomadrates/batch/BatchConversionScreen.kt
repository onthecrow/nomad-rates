package com.onthecrow.nomadrates.batch

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.batch.view.AddCurrencyButtonView
import com.onthecrow.nomadrates.navigation.sharedNavElement
import com.onthecrow.nomadrates.navigation.shrinkDuringSharedTransition
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.ui.negativeAccent
import com.onthecrow.nomadrates.ui.positiveAccent
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.ui.view.AppBarTitleView
import com.onthecrow.nomadrates.ui.view.CurrencyAmountView
import com.onthecrow.nomadrates.ui.view.CurrencyRowView
import com.onthecrow.nomadrates.ui.view.RoundIconButtonDefaults
import com.onthecrow.nomadrates.ui.view.SectionHeaderView
import nomadrates.feature.batch.ui_impl.generated.resources.Res
import nomadrates.feature.batch.ui_impl.generated.resources.batch_conversion_add_currency
import nomadrates.feature.batch.ui_impl.generated.resources.batch_conversion_empty_favourites
import nomadrates.feature.batch.ui_impl.generated.resources.batch_conversion_favourites_header
import nomadrates.feature.batch.ui_impl.generated.resources.batch_conversion_remove_currency
import nomadrates.feature.batch.ui_impl.generated.resources.batch_conversion_title
import nomadrates.feature.batch.ui_impl.generated.resources.ic_delete
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private const val LOADING_ROWS_COUNT = 3
private const val HEADER_KEY = "batch_header"
private const val EMPTY_KEY = "batch_empty"
private const val ADD_BUTTON_KEY = "batch_add"

private const val ADD_BUTTON_HIDE_DURATION_MILLIS = 150

/** Space between the last row of the list and the "Add currency" button. */
private val AddButtonGap = 16.dp

/** How far the "Add currency" button reaches into the amount field from above. */
private val AddButtonFieldOverlap = 12.dp

/** Space around the amount field. */
private val AmountFieldPadding = 16.dp

/**
 * The part of the "Add currency" button that is above the amount field area when the button
 * reaches into the field; the rest of it hangs over the field.
 */
private val AddButtonAboveFieldArea = RoundIconButtonDefaults.Size - AddButtonFieldOverlap - AmountFieldPadding

@Composable
internal fun BatchConversionScreen(
    state: BatchConversionState,
    modifier: Modifier = Modifier,
    onEvent: (BatchConversionEvent) -> Unit = {},
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    // Rendered by the layer that holds both the list and the "Add currency" button, so the button
    // bounces together with the list instead of coming apart from it.
    val overscrollEffect = rememberOverscrollEffect()
    val listBounce = remember(overscrollEffect) { ListBounce(overscrollEffect) }
    var bodyHeight by remember { mutableIntStateOf(0) }
    var fieldAreaHeight by remember { mutableIntStateOf(0) }
    val fieldAreaHeightDp = with(density) { fieldAreaHeight.toDp() }
    // The amount field flies over the "Add currency" button on the way back to the conversion
    // screen, so the button shrinks away first and the transition starts after that.
    val addButtonScale = remember { Animatable(1f) }
    val onBackPress: () -> Unit = {
        scope.launch {
            addButtonScale.animateTo(0f, tween(ADD_BUTTON_HIDE_DURATION_MILLIS))
            onEvent(BatchConversionEvent.OnBackPress)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
    ) {
        AppBarTitleView(
            modifier = Modifier.padding(top = 8.dp),
            title = stringResource(Res.string.batch_conversion_title),
            onBackPress = onBackPress,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // The button may reach into the amount field, but nothing goes into the app bar.
                .clipToBounds()
                .onSizeChanged { size -> bodyHeight = size.height }
                .onPlaced(listBounce::onBodyPlaced),
        ) {
            // Moves with the list when it bounces at its start or end. Everything in it that must
            // stay in place (the amount field, the edge of the list, the pinned button) is moved
            // back by the bounce. The field is inside too: the layer takes the touches of the whole
            // area, so a field next to it would not get any.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .overscroll(overscrollEffect)
                    .onPlaced(listBounce::onLayerPlaced),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = fieldAreaHeightDp)
                        // The list goes under the amount field: rows are cut at a line that does
                        // not move with the bounce.
                        .drawWithContent {
                            val bounce = listBounce.offset()
                            clipRect(bottom = size.height - bounce) {
                                this@drawWithContent.drawContent()
                            }
                        },
                ) {
                    when (state) {
                        is BatchConversionState.Loading -> FavouritesLoading(
                            modifier = Modifier.fillMaxSize(),
                        )

                        is BatchConversionState.Content -> FavouritesList(
                            favourites = state.favourites,
                            listState = listState,
                            overscrollEffect = overscrollEffect?.withoutVisualEffect(),
                            modifier = Modifier.fillMaxSize(),
                            onRemoveCurrency = { currencyCode ->
                                onEvent(BatchConversionEvent.OnRemoveCurrency(currencyCode))
                            },
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset { IntOffset(x = 0, y = -listBounce.offset().roundToInt()) }
                            .height(16.dp)
                            .fillMaxWidth()
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
                CurrencyAmountView(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { translationY = -listBounce.offset() }
                        .onSizeChanged { size -> fieldAreaHeight = size.height }
                        .padding(AmountFieldPadding)
                        .sharedNavElement(BatchConversionSharedElements.AMOUNT_FIELD_KEY),
                    state = state.amountField,
                    onCurrencyClick = { onEvent(BatchConversionEvent.OnBaseCurrencyClick) },
                    onValueChange = { onEvent(BatchConversionEvent.OnAmountChange(it)) },
                )
                if (state is BatchConversionState.Content && bodyHeight > 0 && fieldAreaHeight > 0) {
                    val addButton = rememberAddButtonPlacement(
                        listState = listState,
                        listBottom = (bodyHeight - fieldAreaHeight).toFloat(),
                    )
                    AddCurrencyButtonView(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset {
                                // In the list the button moves with the bounce; pinned to the
                                // field it stays where it is.
                                val top = if (addButton.isPinned) {
                                    addButton.pinnedTop - listBounce.offset()
                                } else {
                                    addButton.listTop()
                                }
                                IntOffset(x = 0, y = top.roundToInt())
                            }
                            .shrinkDuringSharedTransition()
                            .graphicsLayer {
                                scaleX = addButtonScale.value
                                scaleY = addButtonScale.value
                            },
                        text = stringResource(Res.string.batch_conversion_add_currency),
                        morphProgress = addButton.morphProgress,
                        outlineColor = MaterialTheme.colorScheme.background,
                        onClick = { onEvent(BatchConversionEvent.OnAddCurrencyClick) },
                    )
                }
            }
        }
    }
}

/**
 * How far the list layer is moved by the bounce at the start or end of the list (iOS moves the
 * content, other platforms only stretch it, so there it is always 0). Reading [offset] during
 * layout or drawing updates the reader whenever the bounce changes.
 */
@Stable
private class ListBounce(
    private val overscrollEffect: OverscrollEffect?,
) {
    private var bodyCoordinates: LayoutCoordinates? = null
    private var layerCoordinates: LayoutCoordinates? = null

    fun onBodyPlaced(coordinates: LayoutCoordinates) {
        bodyCoordinates = coordinates
    }

    fun onLayerPlaced(coordinates: LayoutCoordinates) {
        layerCoordinates = coordinates
    }

    fun offset(): Float {
        // Only to subscribe the reader to bounce changes; the value itself comes from the layout.
        overscrollEffect?.isInProgress
        val body = bodyCoordinates?.takeIf { it.isAttached } ?: return 0f
        val layer = layerCoordinates?.takeIf { it.isAttached } ?: return 0f
        return body.localPositionOf(layer, Offset.Zero).y
    }
}

/**
 * Where the "Add currency" button is. It follows its place at the end of the list, and when the
 * list is scrolled to the end, that place is right on top of the amount field: the button reaches
 * into the field there. When the list is scrolled up from the end, the place moves down below the
 * field, so the button stays on the field ("pinned") as a round plus button. Scrolling back to the
 * end, the list "picks the button up" again and it turns back into the pill.
 */
@Stable
private class AddButtonPlacement(
    private val listState: LazyListState,
    private val gap: Float,
    /** Top of the button when it is pinned to the amount field. */
    val pinnedTop: Float,
    private val pinThreshold: Float,
) {
    /** Top of the button at its place in the list, or null if that place is below the visible part. */
    private fun listTopOrNull(): Float? = listState.layoutInfo.visibleItemsInfo
        .firstOrNull { item -> item.key == ADD_BUTTON_KEY }
        ?.let { item -> item.offset + gap }

    fun listTop(): Float = listTopOrNull() ?: pinnedTop

    val isPinned: Boolean by derivedStateOf {
        listTopOrNull()?.let { top -> top > pinnedTop + pinThreshold } ?: true
    }

    var morphProgress: Float by mutableFloatStateOf(0f)
}

/**
 * @param listBottom bottom of the list viewport, relative to the screen body.
 */
@Composable
private fun rememberAddButtonPlacement(
    listState: LazyListState,
    listBottom: Float,
): AddButtonPlacement {
    val density = LocalDensity.current
    val placement = remember(listState, listBottom, density) {
        with(density) {
            AddButtonPlacement(
                listState = listState,
                gap = AddButtonGap.toPx(),
                pinnedTop = listBottom - AddButtonAboveFieldArea.toPx(),
                pinThreshold = 1.dp.toPx(),
            )
        }
    }
    placement.morphProgress = animateFloatAsState(
        targetValue = if (placement.isPinned) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "add_currency_morph",
    ).value
    return placement
}

@Composable
private fun FavouritesList(
    favourites: List<BatchConversionListItem>,
    listState: LazyListState,
    overscrollEffect: OverscrollEffect?,
    onRemoveCurrency: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        overscrollEffect = overscrollEffect,
    ) {
        item(key = HEADER_KEY) {
            SectionHeaderView(
                title = stringResource(Res.string.batch_conversion_favourites_header),
            )
        }
        if (favourites.isEmpty()) {
            item(key = EMPTY_KEY) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateItem(),
                    text = stringResource(Res.string.batch_conversion_empty_favourites),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(
            items = favourites,
            key = { it.listKey },
        ) { item ->
            SwipeToRemoveItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                onRemove = { onRemoveCurrency(item.currencyCode) },
            ) {
                CurrencyRowView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    flagIcon = item.flagIcon,
                    currencyCode = item.currencyCode,
                    currencyName = item.currencyName,
                ) {
                    Text(
                        modifier = Modifier.padding(end = 16.dp),
                        text = item.convertedAmount,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.positiveAccent,
                        maxLines = 1,
                    )
                }
            }
        }
        // The button itself is drawn above the list (see AddButtonPlacement); this keeps its place.
        // Only the part of the button above the field area takes space, the rest hangs over the field.
        item(key = ADD_BUTTON_KEY) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AddButtonGap + AddButtonAboveFieldArea),
            )
        }
    }
}

@Composable
private fun SwipeToRemoveItem(
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val swipeState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeState,
        enableDismissFromStartToEnd = false,
        onDismiss = { onRemove() },
        backgroundContent = {
            if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.negativeAccent)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.batch_conversion_remove_currency),
                        tint = MaterialTheme.colorScheme.background,
                    )
                }
            }
        },
    ) {
        content()
    }
}

@Composable
private fun FavouritesLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SectionHeaderView(
            title = stringResource(Res.string.batch_conversion_favourites_header),
        )
        repeat(LOADING_ROWS_COUNT) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shimmer(CircleShape),
                )
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(20.dp)
                        .shimmer(RoundedCornerShape(10.dp)),
                )
            }
        }
    }
}

@Preview
@Composable
private fun BatchConversionScreenPreview() {
    NomadRatesTheme(darkTheme = true) {
        BatchConversionScreen(
            state = BatchConversionState.Content(
                baseCurrency = CurrencyAmountViewState.Content(
                    currencyIcon = "",
                    currencyCode = "USD",
                    conversionValue = "100",
                ),
                favourites = listOf(
                    BatchConversionListItem(
                        flagIcon = "",
                        currencyCode = "EUR",
                        currencyName = "Euro",
                        conversionRate = 0.92,
                        convertedAmount = "92.00",
                    ),
                    BatchConversionListItem(
                        flagIcon = "",
                        currencyCode = "CNY",
                        currencyName = "Chinese Yuan",
                        conversionRate = 7.1,
                        convertedAmount = "710.00",
                    ),
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun BatchConversionScreenLoadingPreview() {
    NomadRatesTheme(darkTheme = true) {
        BatchConversionScreen(state = BatchConversionState.Loading())
    }
}

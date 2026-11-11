package com.onthecrow.nomadrates.batch

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.batch.view.AddCurrencyButtonView
import com.onthecrow.nomadrates.navigation.sharedNavElement
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
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
import kotlin.math.min
import kotlin.math.roundToInt

private const val LOADING_ROWS_COUNT = 3
private const val HEADER_KEY = "batch_header"
private const val EMPTY_KEY = "batch_empty"
private const val ADD_BUTTON_KEY = "batch_add"

/** Space around the "Add currency" button in the list. */
private val AddButtonItemPadding = 16.dp

/** How far the round "Add currency" button reaches into the amount field from above. */
private val AddButtonFieldOverlap = 12.dp

@Composable
internal fun BatchConversionScreen(
    state: BatchConversionState,
    modifier: Modifier = Modifier,
    onEvent: (BatchConversionEvent) -> Unit = {},
) {
    val listState = rememberLazyListState()
    var bodyTop by remember { mutableStateOf<Float?>(null) }
    var fieldTop by remember { mutableStateOf<Float?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
    ) {
        AppBarTitleView(
            modifier = Modifier.padding(top = 8.dp),
            title = stringResource(Res.string.batch_conversion_title),
            onBackPress = { onEvent(BatchConversionEvent.OnBackPress) },
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // The "Add currency" button may reach into the amount field, but not into the app bar.
                .clipToBounds()
                .onGloballyPositioned { coordinates -> bodyTop = coordinates.positionInRoot().y },
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    when (state) {
                        is BatchConversionState.Loading -> FavouritesLoading(
                            modifier = Modifier.fillMaxSize(),
                        )

                        is BatchConversionState.Content -> FavouritesList(
                            favourites = state.favourites,
                            listState = listState,
                            modifier = Modifier.fillMaxSize(),
                            onRemoveCurrency = { currencyCode ->
                                onEvent(BatchConversionEvent.OnRemoveCurrency(currencyCode))
                            },
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
                CurrencyAmountView(
                    modifier = Modifier
                        .padding(16.dp)
                        .onGloballyPositioned { coordinates -> fieldTop = coordinates.positionInRoot().y }
                        .sharedNavElement(BatchConversionSharedElements.AMOUNT_FIELD_KEY),
                    state = state.amountField,
                    onCurrencyClick = { onEvent(BatchConversionEvent.OnBaseCurrencyClick) },
                    onValueChange = { onEvent(BatchConversionEvent.OnAmountChange(it)) },
                )
            }
            val currentBodyTop = bodyTop
            val currentFieldTop = fieldTop
            if (state is BatchConversionState.Content && currentBodyTop != null && currentFieldTop != null) {
                AddCurrencyButton(
                    listState = listState,
                    fieldTop = currentFieldTop - currentBodyTop,
                    onClick = { onEvent(BatchConversionEvent.OnAddCurrencyClick) },
                )
            }
        }
    }
}

/**
 * The "Add currency" button follows its place at the end of the list. When that place moves down
 * to the amount field (or below it), the button stays on top of the field as a round plus button.
 * When the list is scrolled back up, the list "picks the button up" again and it turns back into
 * the pill.
 *
 * @param fieldTop top of the amount field, relative to this box.
 */
@Composable
private fun BoxScope.AddCurrencyButton(
    listState: LazyListState,
    fieldTop: Float,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current
    val itemPadding = with(density) { AddButtonItemPadding.toPx() }
    val pinnedTop = with(density) {
        fieldTop - RoundIconButtonDefaults.Size.toPx() + AddButtonFieldOverlap.toPx()
    }
    val currentPinnedTop by rememberUpdatedState(pinnedTop)

    // Top of the button at its place in the list, or null if that place is below the visible part.
    fun listTop(): Float? = listState.layoutInfo.visibleItemsInfo
        .firstOrNull { item -> item.key == ADD_BUTTON_KEY }
        ?.let { item -> item.offset + itemPadding }

    val isPinned by remember(listState) {
        derivedStateOf { listTop()?.let { top -> top >= currentPinnedTop } ?: true }
    }
    val morphProgress by animateFloatAsState(
        targetValue = if (isPinned) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "add_currency_morph",
    )

    AddCurrencyButtonView(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset {
                val top = listTop()?.let { min(it, currentPinnedTop) } ?: currentPinnedTop
                IntOffset(x = 0, y = top.roundToInt())
            },
        text = stringResource(Res.string.batch_conversion_add_currency),
        morphProgress = morphProgress,
        outlineColor = MaterialTheme.colorScheme.background,
        onClick = onClick,
    )
}

@Composable
private fun FavouritesList(
    favourites: List<BatchConversionListItem>,
    listState: LazyListState,
    onRemoveCurrency: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(bottom = 16.dp),
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
        // The button itself is drawn above the list (see AddCurrencyButton); this keeps its place.
        item(key = ADD_BUTTON_KEY) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(RoundIconButtonDefaults.Size + AddButtonItemPadding * 2),
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
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.batch_conversion_remove_currency),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
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

package com.onthecrow.nomadrates.conversion.batch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.batch.model.BatchConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyViewState
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.view.ConversionCurrencyView
import com.onthecrow.nomadrates.conversion.view.ConversionPanelView
import com.onthecrow.nomadrates.conversion.view.pair.ConversionListItemView
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.ui.view.AppBarTitleView
import com.onthecrow.nomadrates.ui.view.CurrencyRowView
import com.onthecrow.nomadrates.ui.view.RefreshButtonView
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.batch_conversion_add_currency
import nomadrates.feature.conversion.ui_impl.generated.resources.batch_conversion_empty_favourites
import nomadrates.feature.conversion.ui_impl.generated.resources.batch_conversion_favourites_header
import nomadrates.feature.conversion.ui_impl.generated.resources.batch_conversion_title
import org.jetbrains.compose.resources.stringResource

private const val LOADING_ROWS_COUNT = 3
private const val HEADER_KEY = "batch_header"
private const val EMPTY_KEY = "batch_empty"
private const val ADD_BUTTON_KEY = "batch_add"

@Composable
internal fun BatchConversionScreen(
    state: BatchConversionState,
    modifier: Modifier = Modifier,
    onEvent: (BatchConversionEvent) -> Unit = {},
) {
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
                .fillMaxWidth(),
        ) {
            when (state) {
                BatchConversionState.Loading -> FavouritesLoading(
                    modifier = Modifier.fillMaxSize(),
                )

                is BatchConversionState.Content -> FavouritesList(
                    favourites = state.favourites,
                    modifier = Modifier.fillMaxSize(),
                    onAddCurrencyClick = { onEvent(BatchConversionEvent.OnAddCurrencyClick) },
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
        ConversionPanelView {
            ConversionCurrencyView(
                state = (state as? BatchConversionState.Content)?.baseCurrency
                    ?: ConversionCurrencyViewState.Loading,
                onCurrencyClick = { onEvent(BatchConversionEvent.OnBaseCurrencyClick) },
                onValueChange = { onEvent(BatchConversionEvent.OnAmountChange(it)) },
            )
        }
    }
}

@Composable
private fun FavouritesList(
    favourites: List<BatchConversionListItem>,
    onAddCurrencyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item(key = HEADER_KEY) {
            FavouritesHeader()
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
            CurrencyRowView(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                flagIcon = item.flagIcon,
                currencyCode = item.currencyCode,
                currencyName = item.currencyName,
            ) {
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = item.convertedAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
            }
        }
        item(key = ADD_BUTTON_KEY) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .animateItem(),
                contentAlignment = Alignment.Center,
            ) {
                RefreshButtonView(
                    text = stringResource(Res.string.batch_conversion_add_currency),
                    isLoading = false,
                    onClick = onAddCurrencyClick,
                )
            }
        }
    }
}

@Composable
private fun FavouritesHeader(
    modifier: Modifier = Modifier,
) {
    ConversionListItemView(
        modifier = modifier,
        state = ConversionListItem.Header(
            title = stringResource(Res.string.batch_conversion_favourites_header),
        ),
    )
}

@Composable
private fun FavouritesLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        FavouritesHeader()
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
                baseCurrency = ConversionCurrencyViewState.Content(
                    currencyIcon = "",
                    currencyCode = "USD",
                    conversionRate = 1.0,
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
        BatchConversionScreen(state = BatchConversionState.Loading)
    }
}

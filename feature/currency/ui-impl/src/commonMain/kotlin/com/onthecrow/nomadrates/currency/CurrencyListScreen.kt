package com.onthecrow.nomadrates.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.currency.view.CurrencyListItemView
import com.onthecrow.nomadrates.ui.view.AppBarSearchView
import kotlinx.coroutines.launch
import nomadrates.feature.currency.ui_impl.generated.resources.Res
import nomadrates.feature.currency.ui_impl.generated.resources.currency_list_search_bar_hint
import nomadrates.feature.currency.ui_impl.generated.resources.ic_arrow_up
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CurrencyListScreen(
    state: CurrencyListState,
    modifier: Modifier = Modifier,
    onEvent: (CurrencyListEvent) -> Unit = {},
) {
    Box(
        modifier = modifier
            .imePadding(),
    ) {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val showButton by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }
        val layoutDirection = LocalLayoutDirection.current
        val density: Density = LocalDensity.current
        var searchBarHeight by remember { mutableStateOf(0f) }
        val systemBarsPaddingValues = WindowInsets.systemBars.asPaddingValues()
        val contentPaddingValues = remember(searchBarHeight, systemBarsPaddingValues) {
            val height = with(density) {
                searchBarHeight.toDp()
            }
            with(systemBarsPaddingValues) {
                PaddingValues(
                    top = height + 16.dp,
                    bottom = calculateBottomPadding() + 100.dp,
                    start = calculateStartPadding(layoutDirection),
                    end = calculateEndPadding(layoutDirection),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPaddingValues,
            state = listState,
        ) {
            items(
                items = state.currenciesFiltered,
                key = { it.listKey },
                contentType = { it::class },
            ) { currency ->
                CurrencyListItemView(
                    modifier = Modifier.fillMaxWidth()
                        .animateItem(),
                    onItemClick = { currency ->
                        onEvent(CurrencyListEvent.OnCurrencyClick(currency.currencyCode))
                    },
                    onFavouriteClick = { currency ->
                        onEvent(CurrencyListEvent.OnAddToFavouriteClick(currency.currencyCode))
                    },
                    state = currency,
                )
            }
        }

        AppBarSearchView(
            modifier = Modifier.systemBarsPadding()
                .padding(top = 16.dp)
                .onGloballyPositioned {
                    searchBarHeight = it.positionOnScreen().y + it.size.height
                },
            hint = stringResource(Res.string.currency_list_search_bar_hint),
            value = state.searchValue,
            onBackPress = { onEvent(CurrencyListEvent.OnBackPress) },
            onValueChange = { onEvent(CurrencyListEvent.OnSearchValueChange(it)) },
            onClearClick = { onEvent(CurrencyListEvent.OnSearchValueClear) },
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd)
                .systemBarsPadding()
                .padding(32.dp),
            visible = showButton,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = vectorResource(Res.drawable.ic_arrow_up),
                    contentDescription = "Scroll to top",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CurrencyListScreenPreview() {
    CurrencyListScreen(state = CurrencyListState())
}

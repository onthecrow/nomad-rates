package com.onthecrow.nomadrates.currency

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.ui.view.AppBarSearchView
import com.onthecrow.nomadrates.ui.view.CurrencyView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CurrencyListScreen(
    state: CurrencyListState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.imePadding(),
    ) {
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
                    bottom = calculateBottomPadding(),
                    start = calculateStartPadding(layoutDirection),
                    end = calculateEndPadding(layoutDirection),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPaddingValues,
        ) {
            items(
                items = state.currencies,
                key = { it.nameShort },
            ) { currency ->
                CurrencyView(
                    modifier = Modifier.fillMaxWidth(),
                    currencyIcon = currency.flagIcon,
                    currencyCode = currency.nameShort,
                    currencyName = currency.nameLong,
                )
            }
        }

        AppBarSearchView(
            modifier = Modifier.systemBarsPadding()
                .padding(top = 16.dp)
                .onGloballyPositioned { searchBarHeight = it.positionOnScreen().y + it.size.height },
        )
    }
}

@Preview
@Composable
private fun CurrencyListScreenPreview() {
    CurrencyListScreen(state = CurrencyListState())
}

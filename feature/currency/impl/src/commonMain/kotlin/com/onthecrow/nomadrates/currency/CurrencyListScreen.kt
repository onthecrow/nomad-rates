package com.onthecrow.nomadrates.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import nomadrates.feature.currency.impl.generated.resources.Res
import nomadrates.feature.currency.impl.generated.resources.ic_back
import nomadrates.feature.currency.impl.generated.resources.ic_search
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// TODO decompose this
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
        var searchBarHeight by remember { mutableStateOf(0) }
        val systemBarsPaddingValues = WindowInsets.systemBars.asPaddingValues()
        val contentPaddingValues = remember(searchBarHeight, systemBarsPaddingValues) {
            val height = with(density) {
                searchBarHeight.toDp()
            }
            with(systemBarsPaddingValues) {
                PaddingValues(
                    top = calculateTopPadding() + height + 16.dp * 2,
                    bottom = calculateBottomPadding(),
                    start = calculateStartPadding(layoutDirection),
                    end = calculateEndPadding(layoutDirection),
                )
            }
        }

        val radialBrush = remember {
            Brush.radialGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.5f),
                    Color.Transparent,
                ),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPaddingValues,
        ) {
            items(
                count = state.currencies.size,
                key = { index -> state.currencies[index].nameShort },
            ) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(
                            enabled = true,
                            onClick = {}
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = state.currencies[index].flagIcon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Text(text = state.currencies[index].nameShort, color = Color.White)
                    Text(text = state.currencies[index].nameLong, color = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier.systemBarsPadding()
                .padding(top = 16.dp)
                .onGloballyPositioned { searchBarHeight = it.size.height },
        ) {
            Icon(
                modifier = Modifier.size(48.dp)
                    .clip(CircleShape)
                    .background(brush = radialBrush, shape = CircleShape)
                    .clickable(enabled = true, onClick = {})
                    .padding(12.dp),
                imageVector = vectorResource(Res.drawable.ic_back),
                contentDescription = null,
                tint = Color.White,
            )
            val interaction = remember { MutableInteractionSource() }
            val isFocused by interaction.collectIsFocusedAsState()

            BasicTextField(
                modifier = Modifier.weight(1f),
                value = "",
                interactionSource = interaction,
                onValueChange = {},
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.ic_search),
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (/*value*/"".isEmpty() || !isFocused) {
                                    Text(
                                        text = "Search",
                                        fontSize = 16.sp,
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Preview
@Composable
private fun CurrencyListScreenPreview() {
    CurrencyListScreen(state = CurrencyListState())
}

package com.onthecrow.nomadrates.conversion.view.pair

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.onthecrow.nomadrates.conversion.ConversionEvent
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.CurrencyChart
import com.onthecrow.nomadrates.ui.view.LikeButtonView

@Composable
internal fun ConversionListItemView(
    state: ConversionListItem,
    modifier: Modifier = Modifier,
    onClick: (ConversionListItem.Data) -> Unit = {},
    onAddToFavouritesClick: (ConversionListItem.Data) -> Unit = {},
) {
    when (state) {
        is ConversionListItem.Data -> ConversionListItemDataView(
            modifier = modifier,
            state = state,
            onClick = onClick,
            onAddToFavouritesClick = onAddToFavouritesClick,
        )

        is ConversionListItem.Header -> ConversionListItemHeaderView(
            modifier = modifier,
            state = state,
        )
    }
}

@Composable
private fun ConversionListItemHeaderView(
    state: ConversionListItem.Header,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = state.title,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun ConversionListItemDataView(
    state: ConversionListItem.Data,
    modifier: Modifier = Modifier,
    onClick: (ConversionListItem.Data) -> Unit = {},
    onAddToFavouritesClick: (ConversionListItem.Data) -> Unit = {},
) {
    Row(
        modifier = modifier.clickable(enabled = true, onClick = { onClick(state) })
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            AsyncImage(
                modifier = Modifier
                    .height(48.dp)
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.background, CircleShape)
                    .padding(4.dp),
                model = state.fromIcon,
                contentDescription = null,
            )
            AsyncImage(
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp)
                    .height(48.dp)
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.background, CircleShape)
                    .padding(4.dp),
                model = state.toIcon,
                contentDescription = null,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = state.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = state.subtitle,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(72.dp),
        ) {
            CurrencyChart(
                modifier = Modifier.fillMaxSize()
                    .padding(vertical = 16.dp),
                data = state.chartData,
                graphColor = state.chartColor,
                strokeWidth = 1.dp,
            )
            Box(
                modifier = Modifier.fillMaxHeight()
                    .width(60.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background, Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.CenterStart),
            )

            val gradientBaseColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
            val radialBrush = remember {
                Brush.radialGradient(
                    colors = listOf(
                        gradientBaseColor,
                        Color.Transparent,
                    ),
                )
            }
            LikeButtonView(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .background(radialBrush),
                isFavourite = state.isFavourite,
                onClick = { onAddToFavouritesClick(state) },
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionListItemViewPreview(
    @PreviewParameter(ConversionListItemStateProvider::class)
    state: ConversionListItem,
) {
    NomadRatesTheme(darkTheme = true) {
        ConversionListItemView(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        )
    }
}

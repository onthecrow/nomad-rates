package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.ConversionEvent
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyState
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.view.LikeButtonView

@Composable
internal fun ConversionView(
    state: ConversionViewState,
    modifier: Modifier = Modifier,
    onEvent: (ConversionEvent) -> Unit = {},
) {
    if (state.from == null) return
    if (state.to == null) return
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(32.dp),
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(32.dp),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ConversionCurrencyView(
                // todo reduce these fields to single state
                currencyCode = state.from.currencyCode,
                currencyIcon = state.from.currencyIcon,
                value = state.from.conversionValue,
                onCurrencyClick = { onEvent(ConversionEvent.OnFromCurrencyClick) },
                onValueChange = { onEvent(ConversionEvent.OnFromValueChange(it)) },
            )
            ConversionCurrencyView(
                currencyCode = state.to.currencyCode,
                currencyIcon = state.to.currencyIcon,
                value = state.to.conversionValue,
                onCurrencyClick = { onEvent(ConversionEvent.OnToCurrencyClick) },
                onValueChange = { onEvent(ConversionEvent.OnToValueChange(it)) },
            )
        }
        ConversionSwapView(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onEvent(ConversionEvent.OnSwitchButtonPress) },
        )
        LikeButtonView(
            modifier = Modifier.align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.surfaceContainer, shape = CircleShape)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.background, shape = CircleShape),
            isFavourite = state.isFavourite,
            onClick = { onEvent(ConversionEvent.OnActiveConversionPairFavouritesClick) },
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionView(
            state = ConversionViewState(
                from = ConversionCurrencyState("", "", .0, ""),
                to = ConversionCurrencyState("", "", .0, ""),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

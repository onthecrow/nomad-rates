package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.ConversionEvent
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyState
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ConversionView(
    from: ConversionCurrencyState,
    to: ConversionCurrencyState,
    modifier: Modifier = Modifier,
    onEvent: (ConversionEvent) -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ConversionCurrencyView(
                currencyCode = from.currencyCode,
                currencyIcon = from.currencyIcon,
                value = from.conversionValue,
                onCurrencyClick = { onEvent(ConversionEvent.OnFromCurrencyClick) },
                onValueChange = { onEvent(ConversionEvent.OnFromValueChange(it)) },
            )
            ConversionCurrencyView(
                currencyCode = to.currencyCode,
                currencyIcon = to.currencyIcon,
                value = to.conversionValue,
                onCurrencyClick = { onEvent(ConversionEvent.OnToCurrencyClick) },
                onValueChange = { onEvent(ConversionEvent.OnToValueChange(it)) },
            )
        }
        ConversionSwapView(
            modifier = Modifier.align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            onClick = { onEvent(ConversionEvent.OnSwitchButtonPress) },
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
            from = ConversionCurrencyState("", "", .0, ""),
            to = ConversionCurrencyState("", "", .0, ""),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

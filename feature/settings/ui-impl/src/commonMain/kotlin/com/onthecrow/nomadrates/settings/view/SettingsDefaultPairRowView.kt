package com.onthecrow.nomadrates.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.ui.util.toFlagResourceUri
import com.onthecrow.nomadrates.ui.view.CurrencySelectorView
import com.onthecrow.nomadrates.util.toIsoCountryCode

@Composable
internal fun SettingsDefaultPairRowView(
    pair: SelectedConversionPair,
    title: String,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
) {
    val density = LocalDensity.current
    val shouldStackSelectors = with(density) { fontScale > 1.2f }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val stackLayout = shouldStackSelectors || maxWidth < 360.dp

        if (stackLayout) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                DefaultPairSelectorRowView(
                    pair = pair,
                    modifier = Modifier.align(Alignment.End),
                    onFromClick = onFromClick,
                    onToClick = onToClick,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                DefaultPairSelectorRowView(
                    pair = pair,
                    onFromClick = onFromClick,
                    onToClick = onToClick,
                )
            }
        }
    }
}

@Composable
private fun DefaultPairSelectorRowView(
    pair: SelectedConversionPair,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CurrencySelectorView(
            currencyCode = pair.fromCurrencyCode,
            currencyIcon = pair.fromCurrencyCode.toIsoCountryCode().toFlagResourceUri(),
            onClick = onFromClick,
            flagSize = 24.dp,
        )
        Text(
            text = "—",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
        CurrencySelectorView(
            currencyCode = pair.toCurrencyCode,
            currencyIcon = pair.toCurrencyCode.toIsoCountryCode().toFlagResourceUri(),
            onClick = onToClick,
            flagSize = 24.dp,
        )
    }
}

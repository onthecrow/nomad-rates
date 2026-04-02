package com.onthecrow.nomadrates.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.settings.SettingsRatesFreshness
import com.onthecrow.nomadrates.ui.PaleEmerald
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.ui.view.RefreshButtonView

@Composable
internal fun SettingsRatesRowView(
    label: String,
    value: String,
    freshness: SettingsRatesFreshness,
    refreshText: String,
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit,
    onMeasured: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .onGloballyPositioned { onMeasured(it.size.height) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = freshness.toDateColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            RefreshButtonView(
                text = refreshText,
                isLoading = isRefreshing,
                onClick = onRefreshClick,
            )
        }
    }
}

@Composable
private fun SettingsRatesFreshness.toDateColor() = when (this) {
    SettingsRatesFreshness.Fresh -> {
        if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
            PaleEmerald
        } else {
            SageGreen
        }
    }

    SettingsRatesFreshness.Stale -> MaterialTheme.colorScheme.error
    SettingsRatesFreshness.Unknown -> MaterialTheme.colorScheme.onBackground
}

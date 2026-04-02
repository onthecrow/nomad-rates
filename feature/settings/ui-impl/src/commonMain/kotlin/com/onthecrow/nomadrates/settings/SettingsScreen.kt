package com.onthecrow.nomadrates.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.settings.view.SignatureFooterView
import com.onthecrow.nomadrates.ui.PaleEmerald
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.ui.view.AppBarTitleView
import com.onthecrow.nomadrates.ui.view.RefreshButtonView
import com.onthecrow.nomadrates.util.DateUtils
import nomadrates.feature.settings.ui_impl.generated.resources.Res
import nomadrates.feature.settings.ui_impl.generated.resources.settings_privacy_policy
import nomadrates.feature.settings.ui_impl.generated.resources.settings_rates_last_updated_label
import nomadrates.feature.settings.ui_impl.generated.resources.settings_refresh
import nomadrates.feature.settings.ui_impl.generated.resources.settings_title
import nomadrates.feature.settings.ui_impl.generated.resources.settings_unknown_value
import nomadrates.feature.settings.ui_impl.generated.resources.settings_version
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    modifier: Modifier = Modifier,
    onEvent: (SettingsEvent) -> Unit = {},
) {
    val lastUpdatedText = remember(state.lastRatesTimestamp) {
        state.lastRatesTimestamp?.let(DateUtils::formatDateTime)
    }
    val unknownValue = stringResource(Res.string.settings_unknown_value)
    val ratesUpdatedLabel = stringResource(Res.string.settings_rates_last_updated_label)
    val refreshLabel = stringResource(Res.string.settings_refresh)
    val density = LocalDensity.current
    var ratesRowHeightPx by remember { mutableIntStateOf(0) }
    val privacyMinHeight = with(density) {
        maxOf(48.dp, ratesRowHeightPx.toDp())
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AppBarTitleView(
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = 16.dp),
            title = stringResource(Res.string.settings_title),
            onBackPress = { onEvent(SettingsEvent.OnBackPress) },
        )
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SettingsRatesRow(
                    label = ratesUpdatedLabel,
                    value = lastUpdatedText ?: unknownValue,
                    freshness = state.lastRatesFreshness,
                    refreshText = refreshLabel,
                    isRefreshing = state.isRefreshing,
                    onRefreshClick = { onEvent(SettingsEvent.OnRefreshClick) },
                    onMeasured = { ratesRowHeightPx = it },
                )

                PrivacyPolicyRow(
                    minHeight = privacyMinHeight,
                    text = stringResource(Res.string.settings_privacy_policy),
                    onClick = { onEvent(SettingsEvent.OnPrivacyPolicyClick) },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SignatureFooterView()
                    Text(
                        text = stringResource(
                            Res.string.settings_version,
                            state.appVersion.ifBlank { unknownValue },
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRatesRow(
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
                .padding(horizontal = 16.dp),
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
private fun PrivacyPolicyRow(
    minHeight: Dp,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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

@Preview
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            state = SettingsState(
                lastRatesTimestamp = 1_743_387_600L,
                lastRatesFreshness = SettingsRatesFreshness.Fresh,
                appVersion = "1.0",
            )
        )
    }
}

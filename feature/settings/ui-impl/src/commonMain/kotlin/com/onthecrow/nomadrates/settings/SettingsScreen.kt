package com.onthecrow.nomadrates.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.settings.view.SettingsDefaultPairRowView
import com.onthecrow.nomadrates.settings.view.SettingsDialogOption
import com.onthecrow.nomadrates.settings.view.SettingsRatesRowView
import com.onthecrow.nomadrates.settings.view.SettingsSingleChoiceDialogView
import com.onthecrow.nomadrates.settings.view.SettingsSwitchRowView
import com.onthecrow.nomadrates.settings.view.SettingsValueRowView
import com.onthecrow.nomadrates.settings.view.SignatureFooterView
import com.onthecrow.nomadrates.ui.view.AppBarTitleView
import com.onthecrow.nomadrates.util.DateUtils
import com.onthecrow.nomadrates.util.theme.ThemeMode
import nomadrates.feature.settings.ui_impl.generated.resources.Res
import nomadrates.feature.settings.ui_impl.generated.resources.settings_data_source_title
import nomadrates.feature.settings.ui_impl.generated.resources.settings_data_source_value
import nomadrates.feature.settings.ui_impl.generated.resources.settings_default_pair_title
import nomadrates.feature.settings.ui_impl.generated.resources.settings_on_launch
import nomadrates.feature.settings.ui_impl.generated.resources.settings_on_launch_remember_last_pair
import nomadrates.feature.settings.ui_impl.generated.resources.settings_on_launch_use_default_pair
import nomadrates.feature.settings.ui_impl.generated.resources.settings_privacy_policy
import nomadrates.feature.settings.ui_impl.generated.resources.settings_rates_last_updated_label
import nomadrates.feature.settings.ui_impl.generated.resources.settings_refresh
import nomadrates.feature.settings.ui_impl.generated.resources.settings_show_featured_currencies_subtitle
import nomadrates.feature.settings.ui_impl.generated.resources.settings_show_featured_currencies_title
import nomadrates.feature.settings.ui_impl.generated.resources.settings_show_featured_pairs_subtitle
import nomadrates.feature.settings.ui_impl.generated.resources.settings_show_featured_pairs_title
import nomadrates.feature.settings.ui_impl.generated.resources.settings_theme_dark
import nomadrates.feature.settings.ui_impl.generated.resources.settings_theme_light
import nomadrates.feature.settings.ui_impl.generated.resources.settings_theme_system
import nomadrates.feature.settings.ui_impl.generated.resources.settings_theme_title
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
    val uriHandler = LocalUriHandler.current
    val lastUpdatedText = remember(state.lastRatesTimestamp) {
        state.lastRatesTimestamp?.let(DateUtils::formatDateTime)
    }
    val unknownValue = stringResource(Res.string.settings_unknown_value)
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
            ) {
                SettingsRatesRowView(
                    label = stringResource(Res.string.settings_rates_last_updated_label),
                    value = lastUpdatedText ?: unknownValue,
                    freshness = state.lastRatesFreshness,
                    refreshText = stringResource(Res.string.settings_refresh),
                    isRefreshing = state.isRefreshing,
                    onRefreshClick = { onEvent(SettingsEvent.OnRefreshClick) },
                    onMeasured = { ratesRowHeightPx = it },
                )
                SettingsValueRowView(
                    title = stringResource(Res.string.settings_on_launch),
                    subtitle = state.launchPairMode.toLabel(),
                    onClick = { onEvent(SettingsEvent.OnLaunchPairModeClick) },
                )
                AnimatedVisibility(
                    visible = state.launchPairMode == LaunchPairMode.USE_DEFAULT_PAIR,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    SettingsDefaultPairRowView(
                        pair = state.defaultPair,
                        title = stringResource(Res.string.settings_default_pair_title),
                        onFromClick = { onEvent(SettingsEvent.OnDefaultPairFromClick) },
                        onToClick = { onEvent(SettingsEvent.OnDefaultPairToClick) },
                    )
                }
                SettingsSwitchRowView(
                    title = stringResource(Res.string.settings_show_featured_pairs_title),
                    subtitle = stringResource(Res.string.settings_show_featured_pairs_subtitle),
                    checked = state.showFeaturedPairs,
                    onCheckedChange = { onEvent(SettingsEvent.OnShowFeaturedPairsToggle(it)) },
                )
                SettingsSwitchRowView(
                    title = stringResource(Res.string.settings_show_featured_currencies_title),
                    subtitle = stringResource(Res.string.settings_show_featured_currencies_subtitle),
                    checked = state.showFeaturedCurrencies,
                    onCheckedChange = { onEvent(SettingsEvent.OnShowFeaturedCurrenciesToggle(it)) },
                )
                SettingsValueRowView(
                    title = stringResource(Res.string.settings_theme_title),
                    subtitle = state.themeMode.toLabel(),
                    onClick = { onEvent(SettingsEvent.OnThemeClick) },
                )
                SettingsValueRowView(
                    minHeight = privacyMinHeight,
                    title = stringResource(Res.string.settings_privacy_policy),
                    onClick = { onEvent(SettingsEvent.OnPrivacyPolicyClick) },
                )
                SettingsValueRowView(
                    title = stringResource(Res.string.settings_data_source_title),
                    subtitle = stringResource(Res.string.settings_data_source_value),
                    showChevron = false,
                    onClick = { uriHandler.openUri("https://openexchangerates.org/") },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(top = 16.dp, bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SignatureFooterView()
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
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

    SettingsDialogHost(
        state = state,
        onEvent = onEvent,
    )
}

@Composable
private fun SettingsDialogHost(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    when (state.dialogState) {
        SettingsDialogState.LaunchPairModePicker -> {
            SettingsSingleChoiceDialogView(
                title = stringResource(Res.string.settings_on_launch),
                selectedOption = state.launchPairMode,
                options = listOf(
                    SettingsDialogOption(
                        value = LaunchPairMode.REMEMBER_LAST_PAIR,
                        label = stringResource(Res.string.settings_on_launch_remember_last_pair),
                    ),
                    SettingsDialogOption(
                        value = LaunchPairMode.USE_DEFAULT_PAIR,
                        label = stringResource(Res.string.settings_on_launch_use_default_pair),
                    ),
                ),
                onOptionSelected = { onEvent(SettingsEvent.OnLaunchPairModeSelected(it)) },
                onDismissRequest = { onEvent(SettingsEvent.OnDialogStateChanged(null)) },
            )
        }

        SettingsDialogState.ThemePicker -> {
            SettingsSingleChoiceDialogView(
                title = stringResource(Res.string.settings_theme_title),
                selectedOption = state.themeMode,
                options = listOf(
                    SettingsDialogOption(
                        value = ThemeMode.SYSTEM,
                        label = stringResource(Res.string.settings_theme_system),
                    ),
                    SettingsDialogOption(
                        value = ThemeMode.LIGHT,
                        label = stringResource(Res.string.settings_theme_light),
                    ),
                    SettingsDialogOption(
                        value = ThemeMode.DARK,
                        label = stringResource(Res.string.settings_theme_dark),
                    ),
                ),
                onOptionSelected = { onEvent(SettingsEvent.OnThemeModeSelected(it)) },
                onDismissRequest = { onEvent(SettingsEvent.OnDialogStateChanged(null)) },
            )
        }

        null -> Unit
    }
}

@Composable
private fun LaunchPairMode.toLabel(): String = when (this) {
    LaunchPairMode.REMEMBER_LAST_PAIR ->
        stringResource(Res.string.settings_on_launch_remember_last_pair)

    LaunchPairMode.USE_DEFAULT_PAIR ->
        stringResource(Res.string.settings_on_launch_use_default_pair)
}

@Composable
private fun ThemeMode.toLabel(): String = when (this) {
    ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_system)
    ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
    ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            state = SettingsState(
                lastRatesTimestamp = 1_743_387_600L,
                lastRatesFreshness = SettingsRatesFreshness.Fresh,
                launchPairMode = LaunchPairMode.USE_DEFAULT_PAIR,
                defaultPair = SelectedConversionPair("GBP", "JPY"),
                showFeaturedPairs = true,
                showFeaturedCurrencies = true,
                themeMode = ThemeMode.SYSTEM,
                appVersion = "1.0",
            )
        )
    }
}

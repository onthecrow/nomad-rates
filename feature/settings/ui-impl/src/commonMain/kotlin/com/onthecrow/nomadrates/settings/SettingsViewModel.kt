package com.onthecrow.nomadrates.settings

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.CurrencySelectionSource
import com.onthecrow.nomadrates.currency.domain.ObserveLastRatesTimestampUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshRatesManuallyUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.settings.domain.ObserveDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveLaunchPairModeUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveSettingsLinksUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedCurrenciesUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedPairsUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveThemeModeUseCase
import com.onthecrow.nomadrates.settings.domain.SetDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.SetLaunchPairModeUseCase
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedCurrenciesUseCase
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedPairsUseCase
import com.onthecrow.nomadrates.settings.domain.SetThemeModeUseCase
import com.onthecrow.nomadrates.uicore.BaseViewModel
import com.onthecrow.nomadrates.util.ApplicationUtils
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

internal class SettingsViewModel(
    private val navigator: Navigator,
    private val screenResultDispatcher: ScreenResultDispatcher,
    private val observeLastRatesTimestampUseCase: ObserveLastRatesTimestampUseCase,
    private val observeDefaultPairUseCase: ObserveDefaultPairUseCase,
    private val observeLaunchPairModeUseCase: ObserveLaunchPairModeUseCase,
    private val observeSettingsLinksUseCase: ObserveSettingsLinksUseCase,
    private val observeShowFeaturedPairsUseCase: ObserveShowFeaturedPairsUseCase,
    private val observeShowFeaturedCurrenciesUseCase: ObserveShowFeaturedCurrenciesUseCase,
    private val observeThemeModeUseCase: ObserveThemeModeUseCase,
    private val refreshRatesManuallyUseCase: RefreshRatesManuallyUseCase,
    private val setDefaultPairUseCase: SetDefaultPairUseCase,
    private val setLaunchPairModeUseCase: SetLaunchPairModeUseCase,
    private val setShowFeaturedPairsUseCase: SetShowFeaturedPairsUseCase,
    private val setShowFeaturedCurrenciesUseCase: SetShowFeaturedCurrenciesUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    reducer: SettingsReducer,
) : BaseViewModel<SettingsEvent, SettingsState, SettingsReducer>(reducer) {

    init {
        bindEventHandlers()
        observeRatesTimestamp()
        observeSettingsState()
        observeSettingsLinks()
        observeScreenResults()
        loadAppVersion()
    }

    override fun getInitialState(): SettingsState = SettingsState()

    private fun bindEventHandlers() {
        eventFlow.onEach(::handleEvent).launchIn(viewModelScope)
    }

    private fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnBackPress -> navigator.navigateBack()
            SettingsEvent.OnLaunchPairModeClick -> openLaunchPairModePicker()
            SettingsEvent.OnDefaultPairFromClick -> openCurrencySelector(CurrencySelectionSource.SettingsDefaultFrom)
            SettingsEvent.OnDefaultPairToClick -> openCurrencySelector(CurrencySelectionSource.SettingsDefaultTo)
            SettingsEvent.OnThemeClick -> openThemePicker()
            SettingsEvent.OnAboutDataSourceClick -> Unit
            SettingsEvent.OnRefreshClick -> handleRefreshClick()
            is SettingsEvent.OnLaunchPairModeSelected -> applyLaunchPairMode(event.mode)
            is SettingsEvent.OnThemeModeSelected -> applyThemeMode(event.mode)
            is SettingsEvent.OnShowFeaturedPairsToggle -> applyShowFeaturedPairs(event.isEnabled)
            is SettingsEvent.OnShowFeaturedCurrenciesToggle -> applyShowFeaturedCurrencies(event.isEnabled)
            is SettingsEvent.OnAppVersionLoaded,
            is SettingsEvent.OnLaunchPairModeChanged,
            is SettingsEvent.OnDefaultPairChanged,
            is SettingsEvent.OnShowFeaturedPairsChanged,
            is SettingsEvent.OnShowFeaturedCurrenciesChanged,
            is SettingsEvent.OnThemeModeChanged,
            is SettingsEvent.OnDialogStateChanged,
            is SettingsEvent.OnLastRatesFreshnessChanged,
            is SettingsEvent.OnLastRatesTimestampChanged,
            is SettingsEvent.OnRefreshStateChanged,
            is SettingsEvent.OnSettingsLinksChanged -> Unit
        }
    }

    private fun observeRatesTimestamp() {
        observeLastRatesTimestampUseCase()
            .onEach { timestamp ->
                onEvent(SettingsEvent.OnLastRatesTimestampChanged(timestamp))
                onEvent(
                    SettingsEvent.OnLastRatesFreshnessChanged(
                        timestamp.toSettingsRatesFreshness(
                            nowMillis = ApplicationUtils.currentTimeMillis()
                        )
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    private fun observeSettingsState() {
        observeDefaultPairUseCase()
            .onEach { pair -> onEvent(SettingsEvent.OnDefaultPairChanged(pair)) }
            .launchIn(viewModelScope)
        observeLaunchPairModeUseCase()
            .onEach { mode -> onEvent(SettingsEvent.OnLaunchPairModeChanged(mode)) }
            .launchIn(viewModelScope)
        observeShowFeaturedPairsUseCase()
            .onEach { isEnabled -> onEvent(SettingsEvent.OnShowFeaturedPairsChanged(isEnabled)) }
            .launchIn(viewModelScope)
        observeShowFeaturedCurrenciesUseCase()
            .onEach { isEnabled -> onEvent(SettingsEvent.OnShowFeaturedCurrenciesChanged(isEnabled)) }
            .launchIn(viewModelScope)
        observeThemeModeUseCase()
            .onEach { mode -> onEvent(SettingsEvent.OnThemeModeChanged(mode)) }
            .launchIn(viewModelScope)
    }

    private fun observeSettingsLinks() {
        observeSettingsLinksUseCase()
            .onEach { links ->
                onEvent(
                    SettingsEvent.OnSettingsLinksChanged(
                        privacyPolicyUrl = links.privacyPolicyUrl,
                        dataSourceUrl = links.dataSourceUrl,
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    private fun observeScreenResults() {
        screenResultDispatcher.resultFlow
            .onEach { screenResult ->
                when (screenResult) {
                    is CurrencyListScreenResult -> onCurrencySelected(screenResult)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadAppVersion() {
        viewModelScope.launch {
            onEvent(SettingsEvent.OnAppVersionLoaded(ApplicationUtils.getAppVersion()))
        }
    }

    private fun openLaunchPairModePicker() {
        onEvent(SettingsEvent.OnDialogStateChanged(SettingsDialogState.LaunchPairModePicker))
    }

    private fun openThemePicker() {
        onEvent(SettingsEvent.OnDialogStateChanged(SettingsDialogState.ThemePicker))
    }

    private fun openCurrencySelector(source: CurrencySelectionSource) {
        navigator.navigate(CurrencyListDestination(source))
    }

    private fun handleRefreshClick() {
        if (state.value.isRefreshing) return

        viewModelScope.launch {
            onEvent(SettingsEvent.OnRefreshStateChanged(true))
            try {
                refreshRatesManuallyUseCase()
            } finally {
                onEvent(SettingsEvent.OnRefreshStateChanged(false))
            }
        }
    }

    private fun applyLaunchPairMode(mode: com.onthecrow.nomadrates.settings.domain.LaunchPairMode) {
        viewModelScope.launch {
            setLaunchPairModeUseCase(mode)
            onEvent(SettingsEvent.OnDialogStateChanged(null))
        }
    }

    private fun applyThemeMode(mode: com.onthecrow.nomadrates.util.theme.ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode)
            onEvent(SettingsEvent.OnDialogStateChanged(null))
        }
    }

    private fun applyShowFeaturedPairs(isEnabled: Boolean) {
        viewModelScope.launch {
            setShowFeaturedPairsUseCase(isEnabled)
        }
    }

    private fun applyShowFeaturedCurrencies(isEnabled: Boolean) {
        viewModelScope.launch {
            setShowFeaturedCurrenciesUseCase(isEnabled)
        }
    }

    private fun onCurrencySelected(result: CurrencyListScreenResult) {
        val updatedPair = when (result.source) {
            CurrencySelectionSource.SettingsDefaultFrom -> state.value.defaultPair.copy(
                fromCurrencyCode = result.selectedCurrencyCode,
            )

            CurrencySelectionSource.SettingsDefaultTo -> state.value.defaultPair.copy(
                toCurrencyCode = result.selectedCurrencyCode,
            )

            CurrencySelectionSource.ConversionFrom,
            CurrencySelectionSource.ConversionTo -> return
        }

        if (updatedPair == state.value.defaultPair) return

        viewModelScope.launch {
            setDefaultPairUseCase(updatedPair)
        }
    }
}

private val STALE_RATES_THRESHOLD_MILLIS = 27.hours.inWholeMilliseconds

internal fun Long?.toSettingsRatesFreshness(nowMillis: Long): SettingsRatesFreshness {
    if (this == null) return SettingsRatesFreshness.Unknown
    return if (nowMillis - this <= STALE_RATES_THRESHOLD_MILLIS) {
        SettingsRatesFreshness.Fresh
    } else {
        SettingsRatesFreshness.Stale
    }
}

package com.onthecrow.nomadrates.settings

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.currency.domain.ObserveLastRatesTimestampUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshRatesManuallyUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.uicore.BaseViewModel
import com.onthecrow.nomadrates.util.ApplicationUtils
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours

internal class SettingsViewModel(
    private val navigator: Navigator,
    private val observeLastRatesTimestampUseCase: ObserveLastRatesTimestampUseCase,
    private val refreshRatesManuallyUseCase: RefreshRatesManuallyUseCase,
    reducer: SettingsReducer,
) : BaseViewModel<SettingsEvent, SettingsState, SettingsReducer>(reducer) {

    init {
        eventFlow.onEach { event ->
            when (event) {
                SettingsEvent.OnBackPress -> navigator.navigateBack()
                SettingsEvent.OnPrivacyPolicyClick -> Unit
                SettingsEvent.OnRefreshClick -> {
                    if (state.value.isRefreshing) return@onEach
                    viewModelScope.launch {
                        onEvent(SettingsEvent.OnRefreshStateChanged(true))
                        try {
                            refreshRatesManuallyUseCase()
                        } finally {
                            onEvent(SettingsEvent.OnRefreshStateChanged(false))
                        }
                    }
                }
                is SettingsEvent.OnAppVersionLoaded,
                is SettingsEvent.OnLastRatesFreshnessChanged,
                is SettingsEvent.OnLastRatesTimestampChanged,
                is SettingsEvent.OnRefreshStateChanged -> Unit
            }
        }.launchIn(viewModelScope)

        observeLastRatesTimestampUseCase().onEach { timestamp ->
            onEvent(SettingsEvent.OnLastRatesTimestampChanged(timestamp))
            onEvent(
                SettingsEvent.OnLastRatesFreshnessChanged(
                    timestamp.toSettingsRatesFreshness(nowMillis = ApplicationUtils.currentTimeMillis())
                )
            )
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            onEvent(SettingsEvent.OnAppVersionLoaded(ApplicationUtils.getAppVersion()))
        }
    }

    override fun getInitialState(): SettingsState = SettingsState()
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

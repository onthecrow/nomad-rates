package com.onthecrow.nomadrates.conversion

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ConversionViewModel(
    private val navigator: Navigator,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val convertCurrenciesUseCase: ConvertCurrenciesUseCase,
    reducer: ConversionReducer,
    private val screenResultDispatcher: ScreenResultDispatcher,
) : BaseViewModel<ConversionEvent, ConversionState, ConversionReducer>(reducer) {

    private val fromCurrencyCodeStateFlow: MutableStateFlow<String> = MutableStateFlow("USD")
    private val toCurrencyCodeStateFlow: MutableStateFlow<String> = MutableStateFlow("EUR")
    private var conversionCurrencySource: ConversionCurrencySource = ConversionCurrencySource.FROM

    init {
        event.onEach { event ->
            when (event) {
                is ConversionEvent.OnBackPress -> onBackPress()
                is ConversionEvent.OnToCurrencyClick -> onToCurrencyChangeClick()
                is ConversionEvent.OnFromCurrencyClick -> onFromCurrencyChangeClick()
                is ConversionEvent.OnSwitchButtonPress -> onSwitchButtonClick()
                is ConversionEvent.OnFromValueChange -> onFromCurrencyValueChange(event.value)
                is ConversionEvent.OnToValueChange -> onToCurrencyValueChange(event.value)
                else -> {}
            }
        }
            .launchIn(viewModelScope)
        screenResultDispatcher.resultFlow
            .onEach { screenResult ->
                when (screenResult) {
                    is CurrencyListScreenResult -> onCurrencySelected(screenResult.selectedCurrencyCode)
                }
            }
            .launchIn(viewModelScope)

        loadInitialConversionCurrencies()
    }

    override fun getInitialState(): ConversionState = ConversionState()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadInitialConversionCurrencies() {
        combine(
            fromCurrencyCodeStateFlow.flatMapLatest { currencyCode ->
                getCurrencyUseCase(currencyCode).filterNotNull()
            },
            toCurrencyCodeStateFlow.flatMapLatest { currencyCode ->
                getCurrencyUseCase(currencyCode).filterNotNull()
            },
        ) { fromCurrency, toCurrency ->
            onEvent(ConversionEvent.OnFromCurrencyChange(fromCurrency))
            onEvent(ConversionEvent.OnToCurrencyChange(toCurrency))
        }
            .launchIn(viewModelScope)
    }

    private fun onCurrencySelected(selectedCurrencyCode: String) {
        when (conversionCurrencySource) {
            ConversionCurrencySource.FROM -> fromCurrencyCodeStateFlow
            ConversionCurrencySource.TO -> toCurrencyCodeStateFlow
        }
            .update { selectedCurrencyCode }
        onFromCurrencyValueChange(state.value.from?.conversionValue ?: return)
    }

    private fun onFromCurrencyValueChange(value: String) {
        viewModelScope.launch {
            runCatching {
                convertCurrenciesUseCase(
                    fromCurrencyCode = fromCurrencyCodeStateFlow.value,
                    toCurrencyCode = toCurrencyCodeStateFlow.value,
                    amount = value.toDouble(),
                )
            }
                .onSuccess { onEvent(ConversionEvent.OnToValueConverted(it.toString())) }
                .onFailure {
                    // TODO add error handling
                }
        }
    }


    private fun onFromCurrencyChangeClick() {
        conversionCurrencySource = ConversionCurrencySource.FROM
        navigator.navigate(CurrencyListDestination)
    }

    private fun onSwitchButtonClick() {
        val buffer = fromCurrencyCodeStateFlow.value
        fromCurrencyCodeStateFlow.update { toCurrencyCodeStateFlow.value }
        toCurrencyCodeStateFlow.update { buffer }
    }

    private fun onToCurrencyValueChange(value: String) {
        viewModelScope.launch {
            runCatching {
                convertCurrenciesUseCase(
                    fromCurrencyCode = toCurrencyCodeStateFlow.value,
                    toCurrencyCode = fromCurrencyCodeStateFlow.value,
                    amount = value.toDouble(),
                )
            }
                .onSuccess { onEvent(ConversionEvent.OnFromValueConverted(it.toString())) }
                .onFailure {
                    // TODO add error handling
                }
        }
    }

    private fun onToCurrencyChangeClick() {
        conversionCurrencySource = ConversionCurrencySource.TO
        navigator.navigate(CurrencyListDestination)
    }

    private fun onBackPress() {
        navigator.navigateBack()
    }
}

enum class ConversionCurrencySource {
    FROM, TO
}

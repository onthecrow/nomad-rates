package com.onthecrow.nomadrates.conversion.batch

import androidx.lifecycle.viewModelScope
import com.onthecrow.nomadrates.conversion.BatchConversionDestination
import com.onthecrow.nomadrates.conversion.domain.ObserveBatchConversionUseCase
import com.onthecrow.nomadrates.conversion.mapper.filterAmountInput
import com.onthecrow.nomadrates.conversion.domain.SetBatchConversionBaseCurrencyUseCase
import com.onthecrow.nomadrates.conversion.domain.model.BatchConversion
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreenResult
import com.onthecrow.nomadrates.currency.CurrencySelectionSource
import com.onthecrow.nomadrates.currency.domain.AddCurrencyToFavoritesUseCase
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.ScreenResultDispatcher
import com.onthecrow.nomadrates.settings.domain.ObserveUseEnglishCurrencyNamesUseCase
import com.onthecrow.nomadrates.uicore.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class BatchConversionViewModel(
    private val navigator: Navigator,
    private val screenResultDispatcher: ScreenResultDispatcher,
    private val observeBatchConversionUseCase: ObserveBatchConversionUseCase,
    private val observeUseEnglishCurrencyNamesUseCase: ObserveUseEnglishCurrencyNamesUseCase,
    private val setBatchConversionBaseCurrencyUseCase: SetBatchConversionBaseCurrencyUseCase,
    private val addCurrencyToFavoritesUseCase: AddCurrencyToFavoritesUseCase,
    reducer: BatchConversionReducer,
    private val destination: BatchConversionDestination,
) : BaseViewModel<BatchConversionEvent, BatchConversionState, BatchConversionReducer>(reducer) {

    init {
        bindEventHandlers()
        observeBatchConversion()
        observeScreenResults()
    }

    override fun getInitialState(): BatchConversionState = BatchConversionState.Loading()

    private fun bindEventHandlers() {
        eventFlow.onEach(::handleEvent).launchIn(viewModelScope)
    }

    private fun handleEvent(event: BatchConversionEvent) {
        when (event) {
            BatchConversionEvent.OnBackPress -> navigator.navigateBack()
            BatchConversionEvent.OnBaseCurrencyClick -> openCurrencySelector(
                CurrencySelectionSource.BatchConversionBase,
            )
            BatchConversionEvent.OnAddCurrencyClick -> openCurrencySelector(
                CurrencySelectionSource.BatchConversionAddFavourite,
            )
            // Keeps the "from" field of the main screen in sync with this amount.
            is BatchConversionEvent.OnAmountChange -> screenResultDispatcher.dispatch(
                BatchConversionAmountResult(amount = event.value.filterAmountInput()),
            )
            is BatchConversionEvent.OnOpened,
            is BatchConversionEvent.OnBatchConversionChanged -> Unit
        }
    }

    private fun observeBatchConversion() {
        combine<BatchConversion, Boolean, BatchConversionEvent>(
            observeBatchConversionUseCase(),
            observeUseEnglishCurrencyNamesUseCase(),
        ) { batchConversion, useEnglishCurrencyNames ->
            BatchConversionEvent.OnBatchConversionChanged(
                batchConversion = batchConversion,
                useEnglishCurrencyNames = useEnglishCurrencyNames,
            )
        }
            // What the screen was opened with has to be in place before the first data arrives.
            .onStart {
                emit(
                    BatchConversionEvent.OnOpened(
                        baseCurrencyCode = destination.baseCurrencyCode,
                        amount = destination.amount,
                    )
                )
            }
            .onEach(::onEvent)
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

    private fun openCurrencySelector(source: CurrencySelectionSource) {
        navigator.navigate(CurrencyListDestination(source))
    }

    private fun onCurrencySelected(result: CurrencyListScreenResult) {
        when (result.source) {
            CurrencySelectionSource.BatchConversionBase -> viewModelScope.launch {
                setBatchConversionBaseCurrencyUseCase(result.selectedCurrencyCode)
            }

            CurrencySelectionSource.BatchConversionAddFavourite -> viewModelScope.launch {
                addCurrencyToFavoritesUseCase(result.selectedCurrencyCode)
            }

            CurrencySelectionSource.ConversionFrom,
            CurrencySelectionSource.ConversionTo,
            CurrencySelectionSource.SettingsDefaultFrom,
            CurrencySelectionSource.SettingsDefaultTo -> Unit
        }
    }
}
